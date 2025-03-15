# 构建后端（若无需求可选择将下面的命令注释掉）
FROM maven:3.8.6 AS server-build
WORKDIR /app
COPY ./HandSock-Server /app/HandSock-Server
COPY ./Docker/application.yml /app/HandSock-Server/src/main/resources/config/application.yml
WORKDIR /app/HandSock-Server
RUN mvn clean package -DskipTests

# 构建前端（若无需求可选择将下面的命令注释掉）
FROM node:22 AS client-build
WORKDIR /app/HandSock-Client
COPY ./HandSock-Client /app/HandSock-Client
RUN rm -rf /app/HandSock-Client/.env.production
RUN rm -rf /app/HandSock-Client/.env.development
ARG VITE_AUTO_SHOW_IMAGE
ARG VITE_SERVER_IP
ARG VITE_SERVER_URL
ENV VITE_AUTO_SHOW_IMAGE=$VITE_AUTO_SHOW_IMAGE
ENV VITE_SERVER_IP=$VITE_SERVER_IP
ENV VITE_SERVER_URL=$VITE_SERVER_URL
RUN npm install
RUN npm run build

# 运行后端（若无需求可选择将下面的命令注释掉）
FROM openjdk:17-jdk-slim AS server-run
WORKDIR /app
COPY --from=server-build /app/HandSock-Server/target/Handsock2.2.0/* /app/HandSock-Server/server-build/
COPY --from=server-build /app/HandSock-Server/upload/avatar/0/default/* /app/HandSock-Server/server-build/upload/avatar/0/default/
EXPOSE 8081
WORKDIR /app/HandSock-Server/server-build
ENTRYPOINT ["java", "-jar", "handsock-2.2.0.jar"]

# 运行前端（若无需求可选择将下面的命令注释掉）
FROM nginx:alpine AS client-run
WORKDIR /app
COPY ./Docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=client-build /app/HandSock-Client/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]