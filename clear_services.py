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
    cursor.execute("SET FOREIGN_KEY_CHECKS=0;")
    cursor.execute("TRUNCATE TABLE service_prices;")
    cursor.execute("TRUNCATE TABLE dental_services;")
    cursor.execute("SET FOREIGN_KEY_CHECKS=1;")
    conn.commit()
    print("Xóa dữ liệu dịch vụ cũ thành công!")
    cursor.close()
    conn.close()
except Exception as e:
    print(f"Lỗi: {e}")
