# BSDS Assignment 3

###### Shujian Wen, Wenbo Wang

## Results

- Codes are available at my github repo: https://github.com/Greatjian/CS_6650_distributed_system

|                    | 32   | 64   | 128  | 256  |
| ------------------ | ---- | ---- | ---- | ---- |
| aws (assignment 2) | 521  | 555  | 697  | 1127 |
| aws lambda         | 812  | 906  | 1089 | 1296 |
| gcp                | 377  | 423  | 525  | 887  |
| gcp load_balancing | 387  | 414  | 526  | 893  |

- From the table, we can see:
  - gcp (without lb) $\approx$ gcp (with lb) < aws < aws lambda
  - load balancing contributes little work
  - gcp has an upper limit of 2500 throughput at 256 threads (see pic below) 



## AWS Lambda

- eclipse setup

![lambda](/Users/Greatjian/Desktop/NEU/2018_fall/BSDS/assignment3/submission/lambda.png)

- use API gateway to implement requests

![api](/Users/Greatjian/Desktop/NEU/2018_fall/BSDS/assignment3/submission/api.png)

- throughput

![lambda_32](lambda_32.png)

![lambda_64](lambda_64.png)

![lambda_128](lambda_128.png)

![lambda_256](lambda_256.png)

## GCP

- without load balancing

![gcp_32](gcp_32.png)

![image_gcp_32](image_gcp_32.png)



![gcp_64](gcp_64.png)

![image_gcp_64](image_gcp_64.png)

![gcp_128](gcp_128.png)

![image_gcp_128](image_gcp_128.png)

![gcp_256](gcp_256.png)

![image_gcp_256](image_gcp_256.png)



- load balancing setup

![load_balance](load_balance.png)

![gcp_lb_32](gcp_lb_32.png)

![image_gcp_lb_32](image_gcp_lb_32.png)

![gcp_lb_64](gcp_lb_64.png)

![image_gcp_lb_64](image_gcp_lb_64.png)

![gcp_lb_128](gcp_lb_128.png)



![image_gcp_lb_128](image_gcp_lb_128.png)

![gcp_lb_256](gcp_lb_256.png)

![image_gcp_lb_256](image_gcp_lb_256.png)