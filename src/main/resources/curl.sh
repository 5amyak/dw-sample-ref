# Executing admin tasks
curl --location 'http://localhost:8081/tasks/log-level' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'logger=org.example.resources.HelloWorldResource' \
--data-urlencode 'level=DEBUG'

