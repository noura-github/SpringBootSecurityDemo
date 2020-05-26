package technou.com.utilities;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class HibernateAnnotationUtil {

	//This object is very slow & expensive, because it is doing a lot of initialization
	//Create only one copy of it
	private static SessionFactory sessionFactory;
	 
	private static SessionFactory buildSessionFactory() {
	      
	    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure().build();
	    Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
	    SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
	             
	    return sessionFactory;
	}   
	 
	//-----------------------------------------------------
	//Create & return one copy of this object
	public static SessionFactory getSessionFactory() {
	    if(sessionFactory == null) sessionFactory = buildSessionFactory();
	    return sessionFactory;
	}
	//-----------------------------------------------------
	
	/*protected void setup() {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build(); //build StandardServiceRegistry
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception ex) {
			ex.printStackTrace();
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}

	//---------------------------------------------------------------------------------
	protected void exit() {
		sessionFactory.close();
	}*/
}
