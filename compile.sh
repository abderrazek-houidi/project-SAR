cd client 
javac DynamicClient.java
cd ../server
javac GameServer.java
cd ../www
javac *.java
rmic "-v1.1" CallbackImpl
rmic "-v1.1" GameFactoryImpl
rmic "-v1.1" GameImpl
cp ./* C:/xampp/htdocs/TicTacToe