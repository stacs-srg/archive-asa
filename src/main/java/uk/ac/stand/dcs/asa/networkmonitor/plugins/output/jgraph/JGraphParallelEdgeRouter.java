/*
 * Copyright (c) 2001-2005, Gaudenz Alder
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins.output.jgraph;

import org.jgraph.graph.*;

import java.awt.geom.Point2D;

public class JGraphParallelEdgeRouter implements Edge.Routing {

	protected static GraphModel emptyModel = new DefaultGraphModel();

	public static JGraphParallelEdgeRouter sharedInstance = new JGraphParallelEdgeRouter();
	
	/**
	 * The distance between the control point and the middle line. A larger
	 * number will lead to a more "bubbly" appearance of the bezier edges.
	 */
	public double edgeSeparation = 30;

	private JGraphParallelEdgeRouter() {
		// empty
	}

	/**
	 * Returns the array of parallel edges.
	 */
	public Object[] getParallelEdges(EdgeView edge) {
		// FIXME: The model is stored in the cells only in the default
		// implementations. Otherwise we must use the real model here.
		return DefaultGraphModel.getEdgesBetween(emptyModel, edge.getSource()
				.getParentView().getCell(), edge.getTarget().getParentView()
				.getCell(), false);
	}

	public void route(EdgeView edge, java.util.List points) {
		if (edge.getSource() == null || edge.getTarget() == null
				|| edge.getSource().getParentView() == null
				|| edge.getTarget().getParentView() == null)
			return;
		Object[] edges = getParallelEdges(edge);
		// Find the position of the current edge that we are currently routing
		if (edges == null)
			return;
		int position = 0;
		for (int i = 0; i < edges.length; i++) {
			Object e = edges[i];
			if (e == edge.getCell()) {
				position = i;
			}
		}

		// If there is only 1 edge between the two vertices, we don't need this
		// special routing
		if (edges.length < 2) {
			if (points.size() > 2) {
				points.remove(1);
			}
			return;
		}

		// Find the end point positions
		// int n = points.size();
		Point2D from = ((PortView) edge.getSource()).getLocation();
		Point2D to = ((PortView) edge.getTarget()).getLocation();

		if (from != null && to != null) {
			// double dy = from.getY() - to.getY();
			// double dx = from.getX() - to.getX();

			// calculate mid-point of the main edge
			double midX = Math.min(from.getX(), to.getX())
					+ Math.abs((from.getX() - to.getX()) / 2);
			double midY = Math.min(from.getY(), to.getY())
					+ Math.abs((from.getY() - to.getY()) / 2);

			// compute the normal slope. The normal of a slope is the negative
			// inverse of the original slope.
			double m = (from.getY() - to.getY()) / (from.getX() - to.getX());
			double theta = Math.atan(-1 / m);

			// modify the location of the control point along the axis of the
			// normal using the edge position
			double r = edgeSeparation * (Math.floor(position / 2) + 1);
			if ((position % 2) == 0) {
				r = -r;
			}

			// convert polar coordinates to cartesian and translate axis to the
			// mid-point
			double ex = r * Math.cos(theta) + midX;
			double ey = r * Math.sin(theta) + midY;
			Point2D controlPoint = new Point2D.Double(ex, ey);

			// add the control point to the points list
			if (points.size() == 2) {
				points.add(1, controlPoint);
			} else {
				points.set(1, controlPoint);
			}
		}
	}

	/**
	 * @return Returns the edgeSeparation.
	 */
	public double getEdgeSeparation() {
		return edgeSeparation;
	}

	/**
	 * @param edgeSeparation
	 *            The edgeSeparation to set.
	 */
	public void setEdgeSeparation(double edgeSeparation) {
		this.edgeSeparation = edgeSeparation;
	}
	/**
	 * @return Returns the sharedInstance.
	 */
	public static JGraphParallelEdgeRouter getSharedInstance() {
		return sharedInstance;
	}
}