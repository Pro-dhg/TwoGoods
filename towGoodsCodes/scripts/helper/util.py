# -*- coding:utf-8 -*-
import datetime
import json
import os
import re
from pathlib import Path
from dateutil.relativedelta import relativedelta

import yaml

time_format = "%Y-%m-%d %H:%M:%S"


def del_file(filepath):
    """
    删除某一目录下的所有文件或文件夹
    :param filepath: 路径
    :return:
    """
    del_list = os.listdir(filepath)
    for f in del_list:
        file_path = os.path.join(filepath, f)
        if os.path.isfile(file_path):
            os.remove(file_path)
        elif os.path.isdir(file_path):
            return del_file(file_path)


def add_dict_by_dot(dt: dict, key: str, value):
    if not dt or type(dt) != dict:
        dt = dict()

    if "." not in key:
        dt[key]=value
        return dt
    else:
        i = key.index(".")
        k = key[0:i]
        dt[k] = add_dict_by_dot(dt.get(key, {}), key[i + 1:], value)

    return dt


def get_dict_value_by_dot(dt, key: str):
    """
    以properties配置文件的方式,以点号分隔key获取配置文件value
    :return: key列表的值,没有则None
    """
    # 如果字段为空直接返回
    if not dt:
        return None

    if "." not in key:
        return dt[key] if key in dt else None
    else:
        i = key.index(".")
        k = key[0:i]
        return get_dict_value_by_dot(dt[k], key[i + 1:])


def get_dict_value(dt: dict, key: str):
    """
    以properties配置文件的方式,以点号分隔key获取配置文件value
    :return: key列表的值,没有则None
    """
    return get_dict_value_by_dot(dt, key)


def get_dict_value_by_key_list(dt, key_list):
    """
    根据Key列表从字典中获取数据
    :param dt:
    :param key_list:
    :return:
    """
    if not key_list:
        return dt
    else:
        for i in range(len(key_list)):
            key = key_list[i]
            if dt.__contains__(key):
                return get_dict_value_by_key_list(dt[key], key_list[i + 1:])
            else:
                return None




def find_relative_path(relative_path, parent_path=Path(__file__), iteration_count=3):
    """
    解析配置文件路径

    在不同的脚本中执行会
    导致相对路径找不到文件
    可以调用此方法
    :param iteration_count:
    :param parent_path:
    :param relative_path:
    :return:
    """

    rp = Path(relative_path)

    if parent_path.joinpath(rp).is_file():
        return parent_path.joinpath(rp).absolute()
    elif iteration_count <= 0:
        return None
    else:
        return find_relative_path(relative_path, parent_path.parent, iteration_count - 1)


def save_file(obj: object, file_path: str, write_mode="w"):
    """
    save object to file
    object can be list, dict, json, str,
    if object is entity it must be serializable
        eg: clz.__dict__

    :param write_mode: w, wb
    :param obj:
    :param file_path: absolute file path
    :return:
    """
    p = Path(file_path)
    p.parent.absolute().mkdir(parents=True, exist_ok=True)
    s = p.suffix[1:]
    file_type = "yaml" if s in ["yaml", "yml"] else "json"

    if not isinstance(obj, (str, list, tuple, dict)):
        obj = obj.__dict__

    if "json" == file_type:
        with open(file_path, write_mode, encoding="utf-8") as f:
            json.dump(obj, f, indent=4)
    elif "yaml" == file_type:
        with open(file_path, write_mode, encoding="utf-8") as f:
            yaml.dump(obj, f, indent=4)
    return True


def load_file(file_path, clz=None):
    """
    read file to python object
    default dict, list, str

    also you can specify entity
    :param file_path:
    :param clz:
    :return:
    """
    p = Path(file_path)
    if not p.exists() or not p.is_file():
        return None
    file_type = p.suffix[1:]
    if file_type in ["yaml", "yml"]:
        file_type = "yml"
    elif file_type in ["prop", "properties"]:
        file_type="properties"

    dt = dict()

    size = os.path.getsize(p.absolute())

    if size == 0:
        return none_type(clz)

    if "json" == file_type:
        with open(p.absolute(), "r", encoding="utf-8") as fp:
            dt = json.load(fp)
    elif "yml" == file_type:
        with open(p.absolute(), "r", encoding="utf-8") as fp:
            dt = yaml.load(fp, Loader=yaml.FullLoader)
    elif "properties" == file_type:
        dt = load_properties(p.absolute())
    else:
        print(f"only discern yaml/json, file name is: {file_path}")

    if clz and dt and clz not in [dict, list, tuple, str, int, float, bool]:
        return clz(**dt)
    return dt


def load_properties(file_path=None):
    file_path = Path(file_path)
    if not file_path.exists():
        return None
    properties = {}
    with open(file_path, 'r') as f:
        line = f.readline()
        line = line.strip()
        if line.find('=') > 0 and not line.startswith('#'):
            k, v = line.split('=')
            k = k.strip()
            if "," in v:
                v = [int(e.strip()) if e.strip().isdigit() else e.strip() for e in v.split(",")]
            elif v.strip().isdigit():
                v = int(v.strip())
            else:
                v = v.strip()
            properties = add_dict_by_dot(properties, k, v)

    return properties


def none_type(class_type=str):
    """
    return specity type empty
    dict --> {}
    list --> []
    tuple --> ()
    other --> None
    :param class_type:
    :return:
    """
    if class_type == dict:
        return dict()

    if class_type == list:
        return list()

    if class_type == tuple:
        return tuple()

    if class_type == str:
        return str()

    if class_type == int:
        return int()

    if class_type == float:
        return float()

    return None


def update_entity(obj, dt: dict):
    """
    update entity by dict
    if key is id, pass
    if value is empty, pass
    :param obj:
    :param dt:
    :return:
    """
    id_list = [
        "id", "uuid", "aid", "bid", "cid", "did", "eid", "fid", "gid", "hid", "iid", "jid", "kid", "lid",
        "mid", "nid", "oid", "qid", "rid", "sid", "tid", "uid", "vid", "wid", "xid", "yid", "zid"
    ]
    for k, v in dt.items():
        if hasattr(obj, k) and k not in id_list:
            if isinstance(v, int):
                setattr(obj, k, v)
                continue

            if v:
                setattr(obj, k, v)

    return obj


def get_conf_file(conf_file):
    """
    根据给定的文件名定位配置文件位置
    默认的配置文件所在目录为config, conf, resources
    如果没有传参数,则使用默认的配置文件名
    默认的配置文件名为: conf, config, application, app
    默认的配置文件后缀为: yml, json, properties
    权重最高的为config/conf.yml
    权重最低的为resources/appp.properties
    如果都没有找到则返回空
    :param conf_file: 配置文件名,如果没有传参的话使用默认配置
    :return:
    """
    temp_file = None

    # 默认的配置文件路径
    default_conf_file_dirs = ["config", "conf", "resources"]
    # 默认的配置文件名
    default_conf_file_name = ["conf", "config", "application", "app"]
    # 默认的配置文件后缀
    default_conf_file_type = ["yml", "json", "properties"]

    # 如果传参数则在默认的配置文件目录寻找
    if not conf_file:
        for d in default_conf_file_dirs:
            temp_file = find_relative_path(f"{d}/{conf_file}")
            if temp_file:
                return temp_file

    # 如果没有传参则根据默认的配置文件名和后缀去默认的配置文件目录下寻找
    if not temp_file:
        for d in default_conf_file_dirs:
            for c in default_conf_file_name:
                for t in default_conf_file_type:
                    p = f"{d}/{c}.{t}"
                    temp_file = find_relative_path(p)
                    if temp_file:
                        return temp_file
    return None


def parse_crontab_interval(cron: str):
    """
    解析crontab表达式
    返回时间间隔
    :param cron:
    :return:
    """
    cron = cron.strip()
    cron = cron.expandtabs()
    cron = re.sub(' +', ' ', cron)
    i = find_substr_index(cron, " ", 5)
    cron = cron[0:i]
    interval = relativedelta()
    if cron:
        if not re.findall("\d+", cron):
            interval += relativedelta(minutes=1)
        else:
            minutes, hours, days, months, weeks = cron.split(" ")
            minutes = re.sub("\D+", "", minutes)
            minutes = int(minutes) if minutes else 0
            interval += relativedelta(minutes=minutes)

            hours = re.sub("\D+", "", hours)
            hours = int(hours) if hours else 0
            interval += relativedelta(hours=hours)

            days = re.sub("\D+", "", days)
            days = int(days) if days else 0
            interval += relativedelta(days=days)

            months = re.sub("\D+", "", months)
            months = int(months) if months else 0
            interval += relativedelta(months=months)

            weeks = re.sub("\D+", "", weeks)
            weeks = int(weeks) if weeks else 0
            interval += relativedelta(weeks=weeks)
    return interval


def find_substr_index(strings, substr, i):
    """
    根据给定的字符串和子字符串
    判断子字符串在字符串中第i次出现的位置
    :param strings: 父字符串
    :param substr: 子字符串
    :param i: 第几次出现
    :return: 第i次出现的位置(数字)
    """
    index = strings.find(substr)
    if index == -1:
        return -1
    count = 0
    while i > 0:
        index = strings.find(substr)
        if index == -1:
            return count - 1
        else:
            # 第一次出现的位置截止后的字符串
            strings = strings[index + 1:]
            i -= 1
            # 字符串位置数累加
            count = count + index + 1
    return count - 1


def test():
    # print(find_relative_path("config/logger.yml"))

    # save_file({"aa": 11, "bc": 22}, "temp/test.txt")
    # save_file({"aa": 11, "bc": 22}, "temp/test.txt", write_mode="a")
    # save_file("sdf", "temp/test.txt", write_mode="a")
    # save_file(["abc", "sdf", "jkl"], "temp/test.txt", write_mode="a")
    # save_file((123, 234, 345), "temp/test.txt", write_mode="a")
    # save_file(Message(), "temp/test.txt", write_mode="a")
    # save_file(Message(), "temp/test.txt")

    # print(load_file("temp/test.txt", clz=Message).uuid)
    # print(load_file("temp/test.txt"))
    # print(get_dict_value_by_dot(load_file("../config/engine-main.yml"), "config.server.is_config_center"))
    print(load_file("/home/wedo/airflow_scheduler_dependence.json"))


if __name__ == '__main__':
    # test()
    a = "*/1 */2 */1 * * abc"
    b = parse_crontab_interval(a)
    print(b.seconds)
    d = datetime.datetime.now()
    print(d)
    d -= b
    print(d)
