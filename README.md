# FlinkFast Web Backend

## Getting Started

### Local Hacks
1. The volume mount references a location on minikube that the flink
user needs access to. As such, `mininkube ssh` and create a directory
`/tmp/flink` and set permissions so any user can read/write
2. Hit blockers setting up minikube nginx ingress so for now 
use `minikube service` for the rest api and 
the sql gateway.
3. The sql file needs to be stored somewhere. For local,
we would not like to rely on external systems (s3)
so mounting the sql file on the pod will be used during
job creation
4. The docker image used for sql gateway has been modified so
that the sql gateway starts as a background process. Currently
labeled `flink_test_5` in the template

## Spring/Java: API Basic Role-Based Access Control (RBAC) Code Sample

This Java code sample demonstrates **how to implement Role-Based Access Control (RBAC)** in Spring API servers using Auth0.

This code sample is part of the ["Auth0 Developer Resources"](https://developer.auth0.com/resources), a place where you can explore the authentication and authorization features of the Auth0 Identity Platform.

Visit the ["Spring/Java Code Sample: Role-Based Access Control For Basic APIs"](https://developer.auth0.com/resources/code-samples/api/spring/basic-role-based-access-control) page for instructions on how to configure and run this code sample and how to integrate it with a Single-Page Application (SPA) of your choice.

## Why Use Auth0?

Auth0 is a flexible drop-in solution to add authentication and authorization services to your applications. Your team and organization can avoid the cost, time, and risk that come with building your own solution to authenticate and authorize users. We offer tons of guidance and SDKs for you to get started and [integrate Auth0 into your stack easily](https://developer.auth0.com/resources/code-samples/full-stack).
