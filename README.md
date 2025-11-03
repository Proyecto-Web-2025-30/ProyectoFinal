Como ejecutar el proyecto

Backend

En modo desarrollo (H2)
```
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

En modo produccion (base en MV), es necesario usar la VPN
```
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```
Frontend

```
ng serve
```
