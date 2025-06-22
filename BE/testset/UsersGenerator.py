import csv
import random

with open('users.csv', 'w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(['email', 'password', 'name', 'nickname'])

    for i in range(1, 10):
        random_num = random.randint(1, 99999)
        email = f"user{i}_{random_num}@example.com"
        password = f"yourPassword{i}_{random_num}"
        name = f"김소이{i}"
        nickname = f"샹크스{i}_{random_num}"
        writer.writerow([email, password, name, nickname])