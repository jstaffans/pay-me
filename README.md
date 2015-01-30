# pay-me

Pay-Me is a small example of a dummy payment provider service, built with Clojure and [Duct](https://github.com/weavejester/duct). 
The different components are hooked up using core/async channels.  

### Developing

```sh
lein repl
user=> (go)
```

The service now runs on <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and restart the server.

```clojure
user=> (reset)
:reloading (...)
:started
```

### Deployment

Use `lein uberimage` to package the service as a Docker image. 
Note that the `uberimage` plugin expects the Docker API to be available on <http://127.0.0.1:2375>. At least on OS X, 
a [workaround](https://github.com/boot2docker/boot2docker/issues/573) is necessary to expose the API. 

When running in the Docker container, the application is started using a [shell script](https://github.com/jstaffans/pay-me/blob/master/docker/run.sh).
Logging is redirected to `/var/log/pay-me/pay-me.log`. This is simply because this is the pattern of logging I tend to follow
when deploying Docker containers (gather logs on host, store them somewhere).
