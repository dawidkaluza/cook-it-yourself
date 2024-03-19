# Cook it yourself
Simple app aiming to make cooking easier.

# Overview

The plan is to create application based on microservices to get some practise in it.

At the beginning, to keep things simple, the app will look like this:

![High level architecture](./images/high-level-architecture.jpg)

- Web app on front-end side will communicate with back-end services via REST API.
- Services on back-end side will communicate with each other via RabbitMQ asynchronously, to keep them independent of each other.

More details later.



