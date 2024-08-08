# Proof of Concept (PoC): Software Architecture - Software Interfaces **[1]** Interaction Styles - Remote Procedure Call - gRPC

## Description:
This project is intended to evaluate a PoC for the **Remote Procedure Calls Pattern(RPC)** interaction style created by Google (2015) as a Synchronous Messaging Pattern for Cloud
Native Applications that follows the RPC pattern **[3]**.

****

## Objectives:
- Define the ProtoBuffs for 2 services: customer and order.
- Integrate them with a database in which customers and order  information are stored.
- Publish both services to be consumed by a client like BloomRPC **[6]**
- Grant the communication from the customer service to the order service in order to get the total amount for the billing account to pay.

## gRPC Diagram **[5]**:
![gRPC diagram.svg](src%2Fmain%2Fresources%2Fstatic%2FgRPC%20diagram.svg)

****

### Technologies used: 
- **Java 17** as a programming language with maven.
- **grpc-netty-shaded**: **HTTP/2** transport library.
- **grpc-protobuf**, and **protobuf-maven-plugin**: for interpretation of the proto files to the protocolBuffers model to be implemented by the services.
- **Docker** to package the service in a container.

****

### How to run it using Docker?
- The [OrderServer.java](src%2Fmain%2Fjava%2Fcom%2Farchitectural%2Ftrends%2Fgrpc%2Fmicroservice%2Forder%2Fservers%2FOrderServer.java) uses the **port 50052** for both **Unary RPC** and **Server Streaming RPC** calls.
- Go to the **Dockerfile directory** using the **CLI**: [Dockerfile](Dockerfile)
- **Build image**: execute the following command: ```docker build --tag=poc-grpc-project-1.0:1.1.0-beta .```
- **Execute the container**: execute the following command: ``` docker run -p50052:50052 poc-grpc-project-1.0:1.1.0-beta```

### How to run it using Kubernetes?
- Go to the **kubernetes-deployment directory** using the **CLI**: [kubernetes-deployment.yaml](kubernetes-deployment.yaml)
- **Build image**: execute the following command: ```docker build --tag=poc-grpc-project-1.0:1.1.0-beta .```
- **Create the Kubernetes Deployment**: execute the following command: ``` kubectl apply -f .```
- **Port forward the 50052 port**: execute the following command: ```kubectl port-forward poc-grpc-deployment-<pod_name> 50052:50052```

### How to test it?

- Open the project and execute ``` mvn clean install ``` and verify that the protobuf model gets generated as it is shown in the image bellow:

![ProtoBuff model.png](src%2Fmain%2Fresources%2Fstatic%2FProtoBuff%20model.png)

- Open the project and right click in both [CustomerServer.java](src%2Fmain%2Fjava%2Fcom%2Farchitectural%2Ftrends%2Fgrpc%2Fservers%2FCustomerServer.java) and [orderServer.java](src%2Fmain%2Fjava%2Fcom%2Farchitectural%2Ftrends%2Fgrpc%2Fservers%2ForderServer.java) classes.
- Download BloomRPC to be used as a client to make requests to both services **[6]**.
- Load both the [customer.proto](src%2Fmain%2Fproto%2Fcustomer.proto) and the [order_dish.proto](src%2Fmain%2Fproto%2Forder_dish.proto) in the BloomRPC interface:
- Specify the ports for each service as it is shown in the image bellow and execute an unary call per each service. Verify that the _amount proto field that represents the billing account to pay has the value of the sum of all the  associate it with the customer "william"

#### Customer:
![Customer ProtoBuff.png](src%2Fmain%2Fresources%2Fstatic%2FCustomer%20ProtoBuff.png)
****
![gRPC call to CustomerServer.png](src%2Fmain%2Fresources%2Fstatic%2FgRPC%20call%20to%20CustomerServer.png)

#### Order:
![order ProtoBuff.png](src%2Fmain%2Fresources%2Fstatic%2Forder%20ProtoBuff.png)
****
![gRPC call to orderServer.png](src%2Fmain%2Fresources%2Fstatic%2FgRPC%20call%20to%20orderServer.png)

******

### Results:
As it was shown in the previous screenshots, the CustomerService was able to communicate to the order one through a Channel used in the orderClient and the serialization and deserialization was efficiently performed by both services.

******

### Conclusions:
- gRPC was implemented as a Synchronous Pattern of communication as it is suggested in **[3]**.
- However, a more efficient implementation (asynchronous one) is preferred for those kind of applications following the **Microservices Architectural Style**.
- Nevertheless, the implementation of both services allows developers to manage **Software Interfaces Interaction Styles** as it were local methods as they are getting used to.

******
******

## References:

1. [Software Architecture in Practice, 4th Edition, Len Bass, Paul Clements, Rick Kazman, 2022, pages 217-227](https://www.amazon.com/Software-Architecture-Practice-SEI-Engineering/dp/0136886094)
2. [Building Java Microservices with gRPC](https://www.linkedin.com/learning/building-java-microservices-with-grpc)
3. [Design Patterns for Cloud Native Applications, 1st edition, Kasun Indrasiri, Sriskandarajah Suhothayan, 2021, pages 57-72](https://www.amazon.com/Design-Patterns-Cloud-Native-Applications/dp/1492090719)
4. [gRPC Up & Running, 1st Edition, Kasun Indrasiri, Danesh Kuruppu , 2020](https://www.amazon.com/gRPC-Running-Building-Applications-Kubernetes/dp/1492058335)
5. [Introduction to gRPC](https://grpc.io/docs/what-is-grpc/introduction/)
6. [BloomRPC client](https://github.com/bloomrpc/bloomrpc)
7. [Docker In Practice, 2nd Edition, Ian Miell, Aidan Hobson Sayers, 2019](https://www.manning.com/books/docker-in-practice-second-edition)
8. [Cloud Native DevOps with Kubernetes, 1st Edition, John Arundel, Justin Domingus, 2019](https://www.amazon.com/Cloud-Native-DevOps-Kubernetes-Applications/dp/1492040762)
9. [Cloud Native Spring In Action, 1st Edition, Thomas Vitale, 2023](https://www.manning.com/books/cloud-native-spring-in-action)
10. [Cloud Native Buildpacks (CNB)](https://buildpacks.io/docs/)