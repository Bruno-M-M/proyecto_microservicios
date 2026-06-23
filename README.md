Version 1.12

Correxion de errores en feingCliente de todos los microservicios. 
Se migro application.properties a application.yml en microservicio de pago y carrito. 
Se eliminaron imports duplicados de multiples clases. 
Se corregio el url de carrito, en eureka no redirigia a swagger. 
Se modificaron pom.xml de carrito y pago. 
Se actualizo BoletaService, BoletaControllerTest y BoletaServiceTest. 
Se actualizo CarritoController y carritoService. 
Se modifico de application-dev.yml el dll-auto update a create-drop. 
Correxion de DataFaker en el DataLoader de Carrito fallaba al realizar el GET de todo los carritos por que generaba pedidos con id de cliente y/o productos no existentes en la base de datos de las mencionadas, se corrigio asignandole valores dentro de los ya existentes. 
Se cambio en el microservicio de cliente la variable de "contraseña" a "contrasenia". 
Se instalo docker en cliente e inventario. 
Se crearon las clases de test en cliente e inventario. 

Funciona completamente en esta version.
