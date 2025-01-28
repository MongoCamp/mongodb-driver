docker rm -f mongodb;
docker run -d --publish 27017:27017 --name mongodb  mongocamp/mongodb:latest;


#
# [error] Error during tests:
# [error]         dev.mongocamp.driver.mongodb.sync.SyncSpec
# [error]         dev.mongocamp.driver.mongodb.sql.SelectSqlSpec
# [error]         dev.mongocamp.driver.mongodb.dao.StudentDAOSpec
# [error]         dev.mongocamp.driver.mongodb.sql.OtherSqlSpec