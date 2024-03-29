package com.test.events;

import java.net.URI;
import java.util.Scanner;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class UpdaterThread extends Job
{
	private String userData;
	private static final String URL = "http://talos.dcs.bbk.ac.uk:8888/dataUpdateService";

	// Get UISynchronize injected as field
	
	public UpdaterThread(String name, String userData)
	{
		super(name);
		this.userData=userData;
	}
	
	private static URI getBaseURI()
	{
		return UriBuilder.fromUri(URL).build((Object)null);
	}	
	
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{	
		try
		{
			ClientConfig configuration=new DefaultClientConfig();
			Client client=Client.create(configuration);
			WebResource service=client.resource(getBaseURI());
			ClientResponse response=(ClientResponse)service.path("rest").path("lkl").accept(new String[]{"text/plain"}).put(ClientResponse.class,userData);
			System.out.println(response.toString());
			Scanner stream=new Scanner(response.getEntityInputStream());
			
			while(stream.hasNext())
			{
				System.out.println(stream.nextLine());
			}
			
			stream.close();
		}
		catch (UniformInterfaceException e)
		{
			return Status.CANCEL_STATUS;
		}
		catch (Exception e)
		{
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}
}
