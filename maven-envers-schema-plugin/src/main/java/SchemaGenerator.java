import java.util.Collection;

import javax.persistence.Entity;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.EnversSchemaGenerator;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo(name="execute", defaultPhase=LifecyclePhase.COMPILE, configurator = "include-project-dependencies", requiresDependencyResolution=ResolutionScope.RUNTIME)
public class SchemaGenerator extends AbstractMojo {
  private static final String DIALECT = "org.hibernate.dialect.MySQL5Dialect";
  private static final String PACKAGE = "de.dtv.volleyball.persistence";
  private static final String FILE = "target/schema.sql";
  
  private static final Logger LOG = LoggerFactory.getLogger(SchemaGenerator.class);
  
  private Configuration config = new Configuration();
  
  public void execute()
  {
    LOG.info("Generating schema for dialect {}.", DIALECT);
    this.config.setProperty("hibernate.dialect", DIALECT);

    Collection<Class<?>> annotatedClasses = getAnnotatedClasses();
    LOG.info("{} JPA entities found.", annotatedClasses.size());
    for(Class<?> c : annotatedClasses)
    {
      this.config.addAnnotatedClass(c);
    }
    
    SchemaExport export = new EnversSchemaGenerator(config).export();
    export.setOutputFile(FILE).execute(true, false, false, false);
    
    LOG.info("Generated schema saved in {}.", FILE);
  }
  
  private Collection<Class<?>> getAnnotatedClasses()
  {
    LOG.info("Searching package {} for JPA entities.");
    Reflections reflections = new Reflections(PACKAGE);
    return reflections.getTypesAnnotatedWith(Entity.class);
  }
}
