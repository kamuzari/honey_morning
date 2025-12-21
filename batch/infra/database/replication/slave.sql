CHANGE REPLICATION SOURCE TO
    SOURCE_HOST='honeymorning_db',
    SOURCE_USER='honeymorning_read_only_user',
    SOURCE_PASSWORD='password',
    SOURCE_AUTO_POSITION=1,
    GET_SOURCE_PUBLIC_KEY=1;

START REPLICA;



