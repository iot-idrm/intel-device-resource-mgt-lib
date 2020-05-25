#!/bin/sh

# print cmd
set -v

echo "prepare to replace the ip in the app.jar"
# replace the ip in the routing_tabel inside the app.jar
jar xf /app/app.jar BOOT-INF/classes/routing_table.json
echo "[
  {
    \"inetaddr\": \"${DATAPOINT_HOST}\",
    \"uripath\": \"/dp\"
  },
  {
    \"inetaddr\": \"${META_HOST}\",
    \"uripath\": \"/meta\"
  },
  {
    \"inetaddr\": \"${RESOURCEDIRECTORY_HOST}\",
    \"uripath\": \"/rd\"
  }
]" > BOOT-INF/classes/routing_table.json

cat BOOT-INF/classes/routing_table.json
jar uf /app/app.jar BOOT-INF/classes/routing_table.json

# rm -fr BOOT-INF

# run app
sh -c "java ${JAVA_OPTS} -jar /app/app.jar"