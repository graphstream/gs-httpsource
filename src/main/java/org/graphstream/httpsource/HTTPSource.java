/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 *
 * This file is part of GraphStream <http://graphstream-project.org>.
 *
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 *
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.httpsource;

import org.apache.commons.lang3.math.NumberUtils;
import org.graphstream.stream.SourceBase;

import spark.Route;
import spark.Spark;

/**
 * This source allows to control a graph from a web browser. Control is done
 * calling the following url : <code>http://host/graphId</code> this is a rest
 * api so you have to provide information for all parameters e.g.
 * <code>:id</code>
 * <ul>
 * <li><code>/node</code> with the following http requests
 * <ul >
 * <li><code>/:id</code> post: add a Node</li>
 * <li><code>/:id</code> delete: delete a Node</li>
 * </ul>
 * </li>
 * <li><code>/edge</code> with the following http requests
 * <ul>
 * <li><code>/:id/:from/:to/:directed</code> post: add Edge</li>
 * <li><code>/:id</code> delete: add Edge</li>
 * </ul>
 * <li><code>/step/:step</code> post: take given steps</li>
 *
 * </ul>
 *
 */
public class HTTPSource extends SourceBase {

	/**
	 * Http server.
	 */

	final String graphId;

	/**
	 * Create a new http source. The source will be available on
	 * 'http://localhost/graphId' where graphId is passed as parameter of this
	 * constructor. Also this starts the server already
	 *
	 * @param graphId
	 *            id of the graph
	 * @param port
	 *            port on which server will be bound
	 */
	public HTTPSource(final String graphId, final int port) {
		super(graphId);
		Spark.port(port);
		this.graphId = graphId;
		this.setupRoutes();
	}

	/**
	 * Stop the http server.
	 */
	public void stop() {
		spark.Spark.stop();
	}

	/**
	 * setup rest paths and the actions
	 */
	private void setupRoutes() {

		Spark.path("/" + this.graphId, () -> {
			Spark.path("/node", () -> {
				Spark.post("/:id", this.addNode);
				Spark.delete("/:id", this.deleteNode);
			});
			Spark.path("/edge", () -> {
				Spark.post("/:id/:from/:to/:directed", this.addEdge);
				Spark.delete("/:id", this.deleteEdge);
			});
			Spark.path("/step", () -> {
				Spark.post("/:step", this.takeStep);
			});
		});

	}

	/**
	 * Add Node
	 */
	private final Route addNode = (req, resp) -> {
		this.sendNodeAdded(this.sourceId, req.params(":id"));
		resp.status(200);
		resp.type("text");
		return resp;
	};
	/**
	 * Delete Node
	 */
	private final Route deleteNode = (req, resp) -> {
		this.sendNodeRemoved(this.sourceId, req.params(":id"));
		resp.status(200);
		resp.type("text");
		return resp;
	};
	/**
	 * Add Edge
	 */
	private final Route addEdge = (req, resp) -> {
		this.sendEdgeAdded(this.sourceId, req.params(":id"), req.params(":from"), req.params(":to"),
				Boolean.getBoolean(req.params("directed")));
		resp.status(200);
		resp.type("text");
		return resp;
	};
	/**
	 * Delete Edge
	 */
	private final Route deleteEdge = (req, resp) -> {
		this.sendEdgeRemoved(this.sourceId, req.params(":id"));
		resp.status(200);
		resp.type("text");
		return resp;
	};
	/**
	 * Take given steps
	 */
	private final Route takeStep = (req, resp) -> {
		if (NumberUtils.isCreatable(req.params(":step"))) {
			this.sendStepBegins(this.sourceId, Double.parseDouble(req.params(":step")));
			resp.status(200);
			resp.type("text");
			return resp;
		}
		resp.status(400);
		return resp;

	};

}
