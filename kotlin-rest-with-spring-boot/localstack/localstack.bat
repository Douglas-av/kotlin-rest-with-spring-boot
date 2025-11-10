echo ### Criando Queue(Standard) no SQS do LocalStack...
aws --endpoint http://localhost:4566 sqs create-queue --queue-name book
aws --endpoint http://localhost:4566 sqs send-message --queue-url http://localhost:4566/000000000000/book --message-body "Hello World SQS!!!" --delay-seconds 1
aws --endpoint http://localhost:4566 sqs receive-message --queue-url http://localhost:4566/000000000000/book