#Download base image ubuntu 20.04
FROM ubuntu:20.04

LABEL maintainer = 'Oleksii Osipenko <alexey.osipenko@github.com>'

ENV DEBIAN_FRONTEND=noninteractive

WORKDIR /root

#==================
# General Packages
#------------------
# openjdk-8-jdk
#   Java
# ca-certificates
#   SSL client
# tzdata
#   Timezone
# zip
#   Make a zip file
# unzip
#   Unzip zip file
# curl
#   Transfer data from or to a server
# wget
#   Network downloader
# libqt5webkit5
#   Web content engine (Fix issue in Android)
# libgconf-2-4
#   Required package for chrome and chromedriver to run on Linux
# xvfb
#   X virtual framebuffer
# gnupg
#   Encryption software. It is needed for nodejs
#==================
RUN apt-get -qqy update && \
    apt-get -qqy --no-install-recommends install \
    apt-utils \
    && rm -rf /var/lib/apt/lists/*

RUN apt-get -qqy update && \
    apt-get -qqy --no-install-recommends install \
    build-essential\
    openjdk-11-jdk \
    ca-certificates \
    tzdata \
    zip \
    unzip \
    curl \
    wget \
    libqt5webkit5 \
    libgconf-2-4 \
    xvfb \
    gnupg \
  && rm -rf /var/lib/apt/lists/*

RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y locales

RUN sed -i -e 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen && \
    dpkg-reconfigure --frontend=noninteractive locales && \
    update-locale LANG=en_US.UTF-8

#===============
# Set ENV stuff
#===============
ENV LANG en_US.UTF-8
ENV csvPath="/entry/data.csv"
ENV JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64/jre" \
    PATH=$PATH:$JAVA_HOME/bin

EXPOSE 8080

#==================================
# Fix Issue with timezone mismatch
#==================================
ENV TZ="US/Pacific"
RUN echo "${TZ}" > /etc/timezone

# copy jar and csv
RUN mkdir /entry
WORKDIR /entry
COPY ./target/assignement-1.0.0-SNAPSHOT.jar /entry/assignement-1.0.0-SNAPSHOT.jar
COPY ./target/classes/data.csv /entry/data.csv
COPY ./start-server.sh /entry/start-server.sh
RUN chmod +x /entry/start-server.sh

ENTRYPOINT ["/entry/start-server.sh"]