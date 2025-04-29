cd client 
javac DynamicClient.java
cd ../server
javac GameServer.java
cd ../www
javac *.java
rmic CallbackImpl
rmic GameFactoryImpl
rmic GameImpl
cp ./* C:/xampp/htdocs/TicTacToe