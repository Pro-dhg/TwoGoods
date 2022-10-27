# -*- coding: utf-8 -*-
# @Time: 2022/1/20 下午2:03
# @User: wedo
# @IDE:  PyCharm
# @Author: wedo
# @File: conf_helper.py
from helper.util import *


def get_conf(filename="conf.yml"):
    conf_file = get_conf_file(filename)
    if not conf_file:
        return None
    else:
        conf_file = Path(conf_file)
        return load_file(conf_file.absolute())


conf = get_conf()

if __name__ == '__main__':
    aa = get_conf()
    print(json.dumps(aa, ensure_ascii=False, indent=4))
