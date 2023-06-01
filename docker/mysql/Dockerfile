FROM mysql:8.0.32


COPY init.sql /docker-entrypoint-initdb.d/init.sql

RUN chmod -R 755 /docker-entrypoint-initdb.d/init.sql


WORKDIR /usr/bin


VOLUME /var/log/mysql

VOLUME /var/lib/mysql

VOLUME /etc/mysql/conf.d

#ADD my.cnf /etc/my.cnf

#RUN chmod 644 /etc/my.cnf


EXPOSE 3306


ENV MYSQL_ROOT_PASSWORD=123456


HEALTHCHECK --interval=5s --timeout=3s \
    CMD mysql -h localhost -uroot -p${MYSQL_ROOT_PASSWORD} -e 'SELECT 1' 2>/dev/null || exit 1

CMD ["mysqld"]