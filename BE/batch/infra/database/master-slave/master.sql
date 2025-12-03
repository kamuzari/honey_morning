CREATE USER 'honeymorning_read_only_user'@'%' IDENTIFIED BY 'password';
GRANT REPLICATION SLAVE ON *.* TO 'honeymorning_read_only_user'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;