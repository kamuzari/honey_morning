CREATE SCHEMA IF NOT EXISTS honeymorning DEFAULT CHARACTER SET UTF8mb4;
USE honeymorning;
#==================================-============================================================
# tag
insert into tag (word, is_custom)
values('정치', 0),('경제', 0),('사회', 0),('생활/문화', 0),('IT/과학', 0),
      ('세계', 0),('연예', 0),('스포츠',0)