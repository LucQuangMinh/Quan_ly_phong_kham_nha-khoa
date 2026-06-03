import urllib.request, json, urllib.parse
url = 'http://localhost:8080/api/trackings?role=bac-si&doctorName=' + urllib.parse.quote('BS. Nguyễn Văn A')
req = urllib.request.Request(url, headers={
    'X-Role': 'Bác sĩ'.encode('utf-8'),
    'X-User-Role': 'Bác sĩ'.encode('utf-8'),
    'X-User-Name': 'BS. Nguyễn Văn A'.encode('utf-8'),
    'X-User-Id': '2'
})
try:
    with urllib.request.urlopen(req) as r:
        data = json.loads(r.read().decode('utf-8'))
        print(f"Number of trackings returned: {len(data)}")
        for item in data:
            status = item.get('status', '').encode('unicode_escape').decode('utf-8')
            print(f"TrackingID: {item.get('id')}, Status: {status}")
except Exception as e:
    print('Error:', str(e))
