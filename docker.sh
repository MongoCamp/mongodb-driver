docker rm -f mongodb;
docker run -d --publish 27017:27017 --name mongodb  mongocamp/mongodb:latest;