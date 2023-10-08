LABEL authors="toxyc"

# Debido a que no vamos a compilar (pues ya lo hicimos) solo a ejecutar, usaremos una versión ligera de la JRE.
FROM openjdk:11-jre-alpine

# Creamos un directorio /kotlin dentro de  nuestra imagen.
RUN mkdir /kotlin

# Descomprimimos en la carpeta /kotlin que creamos arriba, el tar que generamos previamente con ./gradlew distTar.
ADD app/build/distributions/app.tar /kotlin

# WORKDIR es como hace un comando cd, es decir, nos pasamos a /kotlin/app.
WORKDIR /kotlin/app

# Ejecutamos la app.
CMD ["bin/app"]