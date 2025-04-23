cd client 
javac CallbackInterface.java
javac CallbackImpl.java
rmic CallbackImpl
cd ../server
cp ../client/CallbackInterface.class .
cp ../client/CallbackImpl_Stub.class .
javac *.java
rmic GameFactoryImpl
rmic GameImpl
cd ../client
cp ../server/GameFactoryInterface.class .
cp ../server/GameFactoryImpl_stub.class .
cp ../server/GameInterface.class .
cp ../server/GameImpl_stub.class .
javac GameClient.java