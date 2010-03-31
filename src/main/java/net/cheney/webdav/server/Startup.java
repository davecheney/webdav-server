package net.cheney.webdav.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import net.cheney.motown.protocol.http.async.HttpServerProtocolFactory;
import net.cheney.motown.server.api.Application;
import net.cheney.rev.protocol.ServerProtocolFactory;
import net.cheney.rev.reactor.Reactor;
import net.cheney.webdav.resource.api.ResourceProvidor;
import net.cheney.webdav.resource.file.FileResourceProvidor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.BasicConfigurator;

public class Startup {

	static {
		BasicConfigurator.configure();
	}

	public static void main(String[] args) throws IOException {
		try {
			CommandLine line = parseCommandLine(args);

			ResourceProvidor providor = createResourceProvidorFromCommandLine(line);
			Application app = new WebDAVApplication(providor);

			ServerProtocolFactory factory = new HttpServerProtocolFactory(app);
			SocketAddress addr = createSocketAddressFromCommandLine(line);
			Reactor.open().listen(addr, factory);
			
			System.out.println("Listening on: "+addr);
			System.out.println("Publishing root: "+getDocumentRootFromCommandLine(line));
		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("dav", buildOptions());
		}
	}

	private static ResourceProvidor createResourceProvidorFromCommandLine(CommandLine line) {
		File root = getDocumentRootFromCommandLine(line);
		return new FileResourceProvidor(root);
	}

	private static File getDocumentRootFromCommandLine(CommandLine line) {
		return new File(line.getOptionValue('r', System.getProperty("user.dir")));
	}

	private static SocketAddress createSocketAddressFromCommandLine(
			CommandLine line) {
		String host = line.getOptionValue('h', "localhost");
		int port = Integer.parseInt(line.getOptionValue('p', "8080"));
		return new InetSocketAddress(host, port);
	}

	private static CommandLine parseCommandLine(String[] args)
			throws ParseException {
		CommandLineParser parser = new PosixParser();
		Options options = buildOptions();
		return parser.parse(options, args);
	}

	private static Options buildOptions() {
		Options options = new Options();

		options.addOption("h", "host", true, "Host name to bind to");
		options.addOption("p", "port", true, "Port to bind to");
		options.addOption("r", "root", true, "Root to serve from");

		return options;
	}

}
