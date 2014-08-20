https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master

SimpleFlatMapper
========

Java library to map flat record - ResultSet, csv - to java object with minimum configuration and low footprint.


========
JdbcMapper

			JdbcMapper<MyObject> mapper = JdbcMapperFactory.newInstance().newMapper(MyObject.class);
			
			PreparedStatement ps = conn.prepareStatement("select * from table");
			
			try {
				ResultSet rs = ps.executeQuery();
				
				mapper.forEach(rs, 
					new Handler<MyObject>() {
						void handle(MyObject object) {
							// do something
						}
					});
			} finally {
				ps.close();
			}
