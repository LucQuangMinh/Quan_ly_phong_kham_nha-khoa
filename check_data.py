import urllib.request, json
try:
    print("--- APPOINTMENTS ---")
    url1 = 'http://localhost:8080/api/patient-appointments?role=admin'
    req1 = urllib.request.Request(url1, headers={'X-Role': 'Admin'.encode('utf-8'), 'X-User-Role': 'Admin'.encode('utf-8')})
    with urllib.request.urlopen(req1) as r:
        apps = json.loads(r.read().decode('utf-8'))
        for a in apps[-5:]: # last 5
            print(f"AppID: {a.get('id')}, DocID: {a.get('doctorId')}, Status: {a.get('status', '').encode('unicode_escape').decode('utf-8')}")
            
    print("\n--- TRACKINGS ---")
    url2 = 'http://localhost:8080/api/trackings?role=admin'
    req2 = urllib.request.Request(url2, headers={'X-Role': 'Admin'.encode('utf-8'), 'X-User-Role': 'Admin'.encode('utf-8')})
    with urllib.request.urlopen(req2) as r:
        trks = json.loads(r.read().decode('utf-8'))
        for t in trks[-5:]: # last 5
            print(f"TrkID: {t.get('id')}, Room: {t.get('room', '').encode('unicode_escape').decode('utf-8')}, Status: {t.get('status', '').encode('unicode_escape').decode('utf-8')}")
except Exception as e:
    print('Error:', str(e))
