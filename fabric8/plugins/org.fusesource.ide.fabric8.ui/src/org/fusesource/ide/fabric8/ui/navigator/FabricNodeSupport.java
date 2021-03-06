/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric8.ui.navigator;

import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.fusesource.ide.commons.tree.GraphableNode;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.ui.ContextMenuProvider;
import org.jboss.tools.jmx.core.tree.Node;

public abstract class FabricNodeSupport extends RefreshableCollectionNode
		implements GraphableNode, ContextMenuProvider, ITabbedPropertySheetPageContributor {

	private static final String CONTRIBUTOR_ID = "org.fusesource.ide.fabric.navigator";
	
	private Fabric fabric;

	public FabricNodeSupport(Node parent, Fabric fabric) {
		super(parent);
		this.fabric = fabric;
	}

	public Fabric getFabric() {
		return fabric;
	}

	public void setFabric(Fabric fabric) {
		this.fabric = fabric;
	}
	
	@Override
	public List<Node> getChildrenGraph() {
		return getFabric().getChildrenGraph();
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.tree.RefreshableNode#refresh()
	 */
	@Override
	public void refresh() {
		if (this instanceof Fabric) {
			super.refresh();
		} else {
			this.getFabric().refresh();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.tree.NodeSupport#getContributorId()
	 */
	@Override
	public String getContributorId() {
		return CONTRIBUTOR_ID;
	}
}