import requests

def ipdizhi(ip):
    if ip in ['127.0.0.1', 'localhost', '::1']:
        print("当前为局域网ip,默认上传成都地址")
        return "成都"
    url =f'https://opendata.baidu.com/api.php?query={ip}&co=&resource_id=6006&oe=utf8'
    response = requests.get(url)
    return response.json()['data'][0]['location']