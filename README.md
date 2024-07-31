# Cook it yourself

Simple app to put some software development skills into practice.

# Overview

## Functionality

From functional point of view, the purpose was to create application that would help in all cooking activities like choosing what to cook for dinner today, check off ingredients required to cook given dish during shopping and so on. 
However, for now, to keep things simple, all you can do is simply manage your recipes in the app.

## Technical details

From technical point of view, the goal was to create application based on microservice architecture with different dependencies as database, asynchronous and synchronous messaging, cache, distributed authentication and authorization and so on, so that it would show how the real application based on microservice architecture could look like.

The app is separated into front-end web application and back-end services, as depicted on the image below.

![High level architecture](./images/high-level-architecture.jpg)

- Every app is containerized via Docker, so it can be started and tested independently.
- Back-end services and their dependencies are configured to work out-of-the box on local Kubernetes cluster so running all services requires only running the cluster and applying prepared config, without any long manual setup.

# Applications

## Web app

- web application based on Next.js with Client & Server Rendering 
- communicates with back-end through REST API via API Gateway

## API Gateway

- secures access to back-end services via OAuth 2 (Confidential client)
- forwards incoming requests to proper services, returns their responses back to the client

## User service

- enables signing up, authentication and authorization
- acts as Authorization server in OAuth 2 dance

## Kitchen service

- service where cook (aka user) can manage its recipes
- acts as Resource server in OAuth 2 dance

Each app has its own README file containing more details about itself.

# Security

Services are secured thanks to OAuth 2 protocol.

![Security](./images/security.png)




