cp ../target/original-Cirilo-0.0.1-SNAPSHOT.jar cirilo.jar
cp ../target/lib/fcrepo-api-0.1.2-SNAPSHOT.jar .
for f in *.jar
do
  jarsigner -tsa http://timestamp.digicert.com -keystore /Users/yoda/.ssh/keystore.jks -storepass atZw-nNciENSGeruqB $f zim.uni-graz.at
done