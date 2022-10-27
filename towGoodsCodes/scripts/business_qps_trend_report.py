# -*- coding: utf-8 -*-
import json
import sys

import requests

from helper.logger_helper import get_logger
from helper.conf_helper import conf
from datetime import datetime
from datetime import timedelta
import statistics

logger = get_logger(__name__)

host = conf["host"]
offset = conf["offset"]
port = 55403


def get_qps(analysis_type="count"):
    """
    测试模块安装
    :return:
    """
    url = f"http://{host}:{port}/service/business/qps/trendReport/v1"
    cur_time = datetime.now()
    cur_time = cur_time.replace(second=0)
    start_time = cur_time + timedelta(minutes=-offset)
    end_time = start_time + timedelta(minutes=-10)
    params = {
        "queryType": "1min",
        "intervalType": "10min",
        # "startTime": start_time.strftime("%Y-%m-%d %H:%M:%S"),
        # "endTime": end_time.strftime("%Y-%m-%d %H:%M:%S"),
        "time_out": 30000
    }
    rsp = requests.get(url, params=params)
    # logger.info(f"rsp.text={rsp.text}")
    dt = json.loads(rsp.text)
    lt: list = dt['data']['total']['result']['data']
    if "count" == analysis_type:
        count_result = len(lt)
        print(count_result)
        return count_result
    elif "avg" == analysis_type:
        lt = lt if len(lt) > 0 else [0]
        avg_result = statistics.mean(lt)
        avg_result = int(avg_result)
        print(avg_result)
        return avg_result
    # logger.info(f"avg_result={avg_result}")
    return -1


if __name__ == '__main__':
    if len(sys.argv) >= 2:
        param = sys.argv[1]
        get_qps(param)
    else:
        get_qps()

