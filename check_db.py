import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        port=3307,
        user="root",
        password="",
        database="quan_ly_phong_kham_nha_khoa"
    )
    cursor = conn.cursor()
    cursor.execute("DESCRIBE users;")
    for row in cursor.fetchall():
        print(row)
    
    cursor.execute("DESCRIBE doctors;")
    for row in cursor.fetchall():
        print(row)
except Exception as e:
    print("Error:", e)
