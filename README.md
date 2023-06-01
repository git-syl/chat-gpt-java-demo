This is a Demo project for OpenAI ChatGPT , using Reactive Streams.  
It supports:
- SSE (Server-Sent Events) stream
- Simple Context history messages for maintaining conversation state
- Accurate token counting.
- Proxy settings for http, socket4 and socket5.
- Spring Boot 3.1.0 with JDK 17 ~ 21 ,minimum JDK 8 required
- (API only,without web UI)

### Main Config:
```
server:
  port: 8051

chat-gpt:
  openAi:
    systemPrompt: "You are ChatGPT,trained by #{yourName}..."
    maxTokens: 4096
    apiKey: 'API_KEY'
  api:
    connectTimeoutMillis: 55000
    responseTimeout: 55000
    readTimeout: 55000
    writeTimeout: 55000
    baseUrl: 'https://api.openai.com'
  proxy:
    enabled: false
    type: http
    host: '127.0.0.1'
    port: 7056
...
    name: 'DB_NAME'
    username: 'DB_USER_NAME'
    password: 'DB_PASSWORD'
    url: 'r2dbc:mysql://localhost:3306'
```

### How to Import IDE
1. Open the chatgpt-java-demo folder in IntelliJ IDEA.
2. Select the pom.xml file import.
3. Wait for the Maven project build to complete.

### How to Run
1. Configure your key in the application.yml file.
2. Run main method in ChatgptReactorApplication.java file


### How to Deploy
1. Run the Maven command: `mvn clean package -DskipTests`
2. Find the jar file in the target folder.
3. Execute the following command in your JDK path:  
`/you-jdk-path/bin/java -jar chatgpt-reactor-0.0.1-SNAPSHOT.jar`

### Docker
1. Run `mvn clean package -DskipTests`,and copy file `cp ./target *.jar ./docker/api`
2. Go to the docker folder in the project: `cd ./docker`
3. Execute the command: ` docker-compose up -d`

### API-DOC

(1) This API is similar to [OpenAI's API reference](https://platform.openai.com/docs/api-reference/completions/create) for completions.create, but wrapped as a service:

```
   @PostMapping(value = "/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> completions(@RequestBody @Valid ChatRequest request) {
        return chatGptService.completionsStr(...request);
    }
```
>curl:

```
curl --location 'http://localhost:8051/chat/completions' \
--header 'Content-Type: application/json' \
--data '{
"model": "gpt-3.5-turbo",
"messages": [{"role": "user", "content": "Say this is a test!"}],
"temperature": 0.7,
"stream":true
}'
```

(2) This API is similar to OpenAI, but it uses WebClient:

```
@PostMapping(value = "/completions-web-client", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> completionsWebClient(@RequestBody @Valid ChatRequest request) {
        ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<>() {};
        return webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(request)
                .header("Authorization", "Bearer " + chatGptConfig.getOpenAi().getApiKey())
                .retrieve()
                .bodyToFlux(type);
    }
```
>curl:
```
curl --location 'http://localhost:8051/chat/completions-web-client' \
--header 'Content-Type: application/json' \
--data '{
"model": "gpt-3.5-turbo",
"messages": [{"role": "user", "content": "Say this is a test!"}],
"temperature": 0.7,
"stream":true
}'
```

(3) The last API ("/chat/chat-by-prompt") will allow you to use a prompt that is configured in your database, and it will also save the access log in the "chat_count" table.  

| id|allow_context| type  | prompt                       |create_time|enabled| model_type |
|---|----|-------|------------------------------|----|----|------------|
| 1 |OPEN| 算五行   | 我有个#{content['name']}帮我分析五行。 |2023-05-24 11:46:56|1| my_app_01  |


```
curl --location 'http://localhost:8051/chat/chat-by-prompt' \
--header 'Content-Type: application/json' \
--data '{
"type": "算五行",
"content": {
"name": "张三"
},
"contextOption": "OPEN",
"history": [
{
"role": "user",
"content": "Who won the world series in 2020?"
},
{
"role": "assistant",
"content": "xxx "
}
]
}'
```