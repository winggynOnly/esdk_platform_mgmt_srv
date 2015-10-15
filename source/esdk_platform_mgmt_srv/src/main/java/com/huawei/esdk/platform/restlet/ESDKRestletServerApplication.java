package com.huawei.esdk.platform.restlet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.apache.commons.digester3.Digester;
import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.xml.sax.SAXException;

public class ESDKRestletServerApplication extends JaxRsApplication
{
    private static final Logger LOGGER = Logger.getLogger(ESDKRestletServerApplication.class);
    
    @SuppressWarnings("unchecked")
    public ESDKRestletServerApplication(Context context)
    {
        super(context);
        
        Class<Application> clazz;
        Constructor<Application> constructor;
        for (ApplicationBean item : getApplicationClasses())
        {
            try
            {
            	LOGGER.debug("Now loading class " + item.getApplicationClass());
                clazz = (Class<Application>)Class.forName(item.getApplicationClass());
                constructor = clazz.getConstructor();
                
                add(constructor.newInstance());
            }
            catch (ClassNotFoundException e)
            {
                LOGGER.error("", e);
            }
            catch (NoSuchMethodException e)
            {
                LOGGER.error("", e);
            }
            catch (SecurityException e)
            {
                LOGGER.error("", e);
            }
            catch (IllegalArgumentException e)
            {
                LOGGER.error("", e);
            }
            catch (InstantiationException e)
            {
                LOGGER.error("", e);
            }
            catch (IllegalAccessException e)
            {
                LOGGER.error("", e);
            }
            catch (InvocationTargetException e)
            {
                LOGGER.error("", e);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<ApplicationBean> getApplicationClasses()
    {
        List<ApplicationBean> result = new ArrayList<ApplicationBean>();
        try
        {
            Digester digest = new Digester();
            digest.setValidating(false);
            digest.addObjectCreate("applications", ArrayList.class);
            digest.addObjectCreate("applications/application", ApplicationBean.class);
            digest.addBeanPropertySetter("applications/application", "applicationClass");
            digest.addSetNext("applications/application", "add");
            
            //Read configuration file
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:META-INF/rest_app*.xml");
            LOGGER.info("##############application count:" + resources.length);
            for (Resource resource : resources)
            {
                LOGGER.debug(resource.getFilename());
                result.addAll((List<ApplicationBean>)digest.parse(resource.getURL()));
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Load Rest Application failed.", e);
        }
        catch (SAXException e)
        {
            LOGGER.error("Load Rest Application failed.", e);
        }
        
        return result;
    }
}
