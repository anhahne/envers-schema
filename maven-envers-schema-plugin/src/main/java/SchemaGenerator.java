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

@Mojo(name="execute", defaultPhase=LifecyclePhase.COMPILE,
      configurator = "include-project-dependencies",
      requiresDependencyResolution=ResolutionScope.RUNTIME)
public class SchemaGenerator extends AbstractMojo {
  private static final String DIALECT = "org.hibernate.dialect.MySQL5Dialect";
  private static final String PACKAGE = "de.dtv.volleyball.persistence";
  private static final String FILE = "target/schema.sql";

  private Configuration config = new Configuration();
  
  @Override
  public void execute()
  {
    getLog().info("Generating schema for dialect " + DIALECT);
    this.config.setProperty("hibernate.dialect", DIALECT);

    Collection<Class<?>> annotatedClasses = getAnnotatedClasses();
    getLog().info(annotatedClasses.size() + " JPA entities found.");
    for(Class<?> c : annotatedClasses)
    {
      this.config.addAnnotatedClass(c);
    }

    SchemaExport export = new EnversSchemaGenerator(this.config).export();
    export.setOutputFile(SchemaGenerator.FILE).execute(true, false, false, false);

    getLog().info("Generated schema saved in " + FILE);
  }

  private Collection<Class<?>> getAnnotatedClasses()
  {
    getLog().info("Searching package " + PACKAGE + " for JPA entities.");
    Reflections reflections = new Reflections(PACKAGE);
    return reflections.getTypesAnnotatedWith(Entity.class);
  }
}
