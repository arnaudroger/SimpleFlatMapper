SimpleFlatMapper
========

Java library to map flat record - ResultSet, csv - to java object with minimum configuration and low footprint.


===== 
Samples

			PreparedStatement ps = conn.prepareStatement("select * from table");
			
			try {
				ResultSet rs = ps.executeQuery();
				
				ResultSetMapperFactory
					.newMapper(MyObject.class, rs.getMetaData())
					.forEach(new Handler<MyObject>() {
						void handle(MyObject object) {
							// do something
						}
				});
			} finally {
				ps.close();
			}
