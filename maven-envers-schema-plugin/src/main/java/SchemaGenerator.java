import java.util.Collection;

import javax.persistence.Entity;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.hibernate.cfg.Configuration;
import org.hibernate.envers.Audited;
import org.hibernate.tool.EnversSchemaGenerator;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.reflections.Reflections;

/**
 * Maven Plugin to generate a DDL from JPA entities which can be annotated with
 * Hibernate Envers annotation {@link Audited}.
 *
 * @author Andreas Hahne
 *
 */
@Mojo(name = "execute", defaultPhase = LifecyclePhase.PACKAGE,
      configurator = "include-project-dependencies",
      requiresDependencyResolution=ResolutionScope.RUNTIME)
public class SchemaGenerator extends AbstractMojo {
  private static final String DEFAULT_DESTINATION = "target/schema.sql";

  private Configuration config = new Configuration();

  @Parameter(required = true)
  private String dialect;

  @Parameter(required = true)
  private String packageName;

  @Parameter(defaultValue = DEFAULT_DESTINATION)
  private String destination;

  @Override
  public void execute()
  {
    getLog().info("Generating schema for dialect " + this.dialect);
    this.config.setProperty("hibernate.dialect", this.dialect);

    Collection<Class<?>> annotatedClasses = getAnnotatedClasses();
    getLog().info(annotatedClasses.size() + " JPA entities found.");
    for(Class<?> c : annotatedClasses)
    {
      this.config.addAnnotatedClass(c);
    }

    SchemaExport export = new EnversSchemaGenerator(this.config).export();
    export.setOutputFile(this.destination).execute(true, false, false, false);

    getLog().info("Generated schema saved in " + this.destination);
  }

  private Collection<Class<?>> getAnnotatedClasses()
  {
    getLog().info("Searching package " + this.packageName + " for JPA entities.");
    Reflections reflections = new Reflections(this.packageName);
    return reflections.getTypesAnnotatedWith(Entity.class);
  }
}
