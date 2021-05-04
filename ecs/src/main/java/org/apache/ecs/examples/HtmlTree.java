/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Jakarta Element Construction Set", 
 *    "Jakarta ECS" , and "Apache Software Foundation" must not be used 
 *    to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Jakarta Element Construction Set" nor "Jakarta ECS" nor may "Apache" 
 *    appear in their names without prior written permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.ecs.examples;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;
import org.apache.ecs.xml.*;
import org.apache.ecs.html.*;

/**
 * This JavaBean returns the hierarchical structure described in a
 * javax.swing.tree.DefaultMutableTreeNode as valid XHTML. This class
 * is a very simple counterpart of the javax.swing.JTree with the
 * exception that the external Controller is integrated into this View.
 * If you want your tree elements (nodes and leafs) to be marked with an
 * anchor, you'll have to make sure that the name of your node contains
 * such an anchor.
 *
 * <p>A specific node in a tree can be identified by a path, described as
 * colon separated integer values (e.g. &quot;0:1&quot;). Unlike Swing's
 * JTree component, this JavaBean expands only the requested node, leaving
 * all its parent nodes collapsed. An <i>expanded</i> node is one displays
 * its children. A <i>collapsed</i> node is one which hides them.</p>
 *
 * <p>You can use this class in a JavaServerPage like this:</p>
 *
 * <p><blockquote>
 *    <p>...template text...</p>
 *    <p><code>
 *      &lt;%-- Get the bean from the request scope or
 *      create a new one if none exists --%&gt;<br />
 *      &lt;jsp:useBean id="tree" scope="request"
 *      class="org.apache.ecs.examples.HtmlTree"&gt;
 *      <blockquote>
 *        <p>&lt;%-- Read the path from the request --%&gt;<br />
 *        &lt;jsp:setProperty name="tree"
 *        property="path" param="path" /&gt;</p>
 *        <p>&lt;%-- Set UI properties --%&gt;<br />
 *        &lt;jsp:setProperty name="tree"
 *        property="rootVisible" value="true" /&gt;<br />
 *        &lt;jsp:setProperty name="tree"
 *        property="indentation" value="2" /&gt;<br />
 *        &lt;jsp:setProperty name="tree"
 *        property="openIcon" value="open.gif" /&gt;<br />
 *        &lt;jsp:setProperty name="tree"
 *        property="closedIcon" value="closed.gif" /&gt;<br />
 *        &lt;jsp:setProperty name="tree"
 *        property="leafIcon" value="leaf.gif" /&gt;</p>
 *        <p>&lt;%-- Workaround for Netscape
 *        and Opera browsers --%&gt;<br />
 *        &lt;jsp:setProperty name="tree"
 *        property="action" value="tree.jsp" /&gt;</p>
 *      </blockquote>
 *      &lt;/jsp:useBean&gt;
 *    </code></p>
 *    <p>...template text...</p>
 *    <p><code>
 *      &lt;%-- Get the XHTML output from the bean --%&gt;<br />
 *      &lt;jsp:getProperty name="tree" property="html" /&gt;
 *    </code></p>
 * </blockquote></p>
 *
 * Although this class is just an example of using parts of the Element
 * Construction Set, you can use it quite well in real web applications.
 *
 * @version 1.1, 2001/12/30, added ability to use a DefaultTreeModel as the
 *                           data model, added some useful methods including
 *                           one for basic HTML encoding, improved javadoc
 *                           comments and fixed minor bugs
 * @version 1.0, 2001/10/30, initial release
 * @author <a href="mailto:horombo@gmx.de">Christian Brensing</a>
 */
public class HtmlTree implements Serializable
{
    /**
     * Number of blank spaces around each hierarchical level
     * will be indented in relation to its parent node.
     * Default is 2.
     */
    protected int indentation = 2;
    
    /**
     * Path to the requested node to be displayed,
     * described as colon separated integer values.
     * Default is an empty String.
     */
    protected String path = "";
    
    /**
     * Parameter name used to build the query string for the
     * anchor that acts as the Controller for opening and
     * closing nodes. Default is &quot;path&quot;.
     */
    protected String parameterName = "path";
    
    /**
     * Array that stores the index of each node
     * read from the path property.
     */
    private int[] pathArray = new int[0];
    
    /**
     * Icon for displaying leafs.
     * Default is an empty String.
     */
    protected String leafIcon = "";
    
    /**
     * Icon for displaying open nodes.
     * Default is an empty String.
     */
    protected String openIcon = "";
    
    /**
     * Icon for displaying closed nodes.
     * Default is an empty String.
     */
    protected String closedIcon = "";
    
    /**
     * Context path of a web resource for the workaround
     * of a strange behaviour of Netscape and Opera
     * browsers. Default is an empty String.
     */
    protected String action = "?";
    
    /**
     * True if the root node is displayed, false if
     * its children are the highest visible nodes.
     * Default is true.
     */
    protected boolean rootVisible = true;
    
    /**
     * The node that defines the tree displayed by this object.
     * Default is a sample model.
     */
    protected DefaultMutableTreeNode root = getDefaultTreeModel();
    
    /**
     * Stores the requested node to be displayed.
     * Default is the root node.
     */
    protected DefaultMutableTreeNode displayNode = root;
    
    /**
     * Returns a HtmlTree with a sample model.
     */
    public HtmlTree()
    {
        // do nothing
    }
    
    /**
     * Returns a HtmlTree with the specified DefaultMutableTreeNode
     * as its root. If the DefaultMutableTreeNode is <tt>null</tt>,
     * a sample model is set automatically.
     *
     * @param root a DefaultMutableTreeNode object
     */
    public HtmlTree(DefaultMutableTreeNode root)
    {
        setRoot(root);
    }
    
    /**
     * Returns a HtmlTree with the specified DefaultTreeModel as
     * its model. If the specified DefaultTreeModel is <tt>null</tt>,
     * a sample model is set automatically.
     *
     * @param model a DefaultTreeModel object
     */
    public HtmlTree(DefaultTreeModel model)
    {
        setModel(model);
    }
    
    /**
     * Sets the root node that will provide the data. If the
     * specified DefaultMutableTreeNode is <tt>null</tt>,
     * a sample model is set automatically.
     *
     * @param root a DefaultMutableTreeNode object
     */
    public void setRoot(DefaultMutableTreeNode root)
    {
        // Check property
        if (root == null) 
        { 
            root = getDefaultTreeModel(); 
        }
    
        // Set property
        this.root = root;
    }
    
    /**
     * Returns the node that defines the tree displayed
     * by this object.
     *
     * @return the root node that provides the data
     */
    public DefaultMutableTreeNode getRoot()
    {
        return root;
    }
    
    /**
     * Sets the tree model that will provide the data. If the
     * specified DefaultTreeModel is <tt>null</tt>, a sample
     * model is set automatically.
     *
     * @param model a DefaultTreeModel object
     */
    public void setModel(DefaultTreeModel model)
    {
        if (model != null)
        {
            setRoot((DefaultMutableTreeNode)
            model.getRoot());
        }
        else
        {
            setRoot(null);
        }
    }
    
    /**
     * Returns the model that defines the tree displayed
     * by this object.
     *
     * @return the model that provides the data
     */
    public DefaultTreeModel getModel()
    {
        return new DefaultTreeModel(root);
    }
    
    /**
     * Sets the path - described as colon separated integer values -
     * to the requested node to be displayed.
     *
     * @param path the path to the requested node to be displayed
     */
    public void setPath(String path)
    {
        try
        {
            // Tokenize the path
            pathArray = getPathResolved(path);
    
            // Set path property
            this.path = path;
        }
        catch (NumberFormatException nfe)
        {
            // Reset path property
            this.path = "";
        }
    }
    
    /**
     * Returns the path to the requested node.
     *
     * @return the path to the requested node
     */
    public String getPath()
    {
        return path;
    }
    
    /**
     * Returns a path constructed from the path array for the
     * specified level.
     *
     * @param level the distance from the node to the root node.
     * @return the path for the specified level
     */
    public String getPath(int level)
    {
        // Check property
        if (level > pathArray.length) 
        { 
            level = pathArray.length; 
        }
    
        // New StringBuffer for the path generation
        StringBuffer autoPath = new StringBuffer();
    
        // For each node
        for (int i = 0; i < level; i++)
        {
            // Add node index (read from the path array)
            autoPath.append(pathArray[i]);
        
            // Add path separator (colon)
            if (i < (level - 1)) 
            { 
                autoPath.append(":"); 
            }
        }
    
        // Return generated path
        return autoPath.toString();
    }
    
    /**
     * Returns an array that stores the index of each
     * node read from the specified path.
     *
     * @return an array of node indices
     * @throws java.lang.NumberFormatException if the specified
     * path does not consist of colon separated integer values
     */
    public static int[] getPathResolved(String path)
        throws NumberFormatException
    {
        try
        {
            // Initialize the token array
            int[] returnArray = new int[0];
        
            // Tokenize the path
            StringTokenizer st = new StringTokenizer(path,":");
        
            // Reset the array with the number of tokens
            returnArray = new int[st.countTokens()];
        
            // Save value of each token in the array
            if (st.countTokens() > 0)
            {
                for (int i = 0; i < returnArray.length; i++)
                {
                    returnArray[i] = Integer.parseInt
                        (st.nextToken().trim());
                }
            }
        
            // Return path array
            return returnArray;
        }
        catch (Exception e)
        {
            // Wrap the exception in a NumberFormatException
            throw new NumberFormatException(e.getMessage());
        }
    }
    
    /**
     * Sets the name of the parameter used to build the query
     * string for the anchor that acts as the Controller for
     * opening and closing nodes. You only have to set this
     * name if the default name &quot;path&quot; is already
     * used by your web application.
     *
     * @param parameterName the name of the parameter used
     * to build the query string
     */
    public void setParameterName(String parameterName)
    {
        // Check property
        if (parameterName == null
        || parameterName.equals(""))
        {
            parameterName = "path";
        }
    
        // Set property
        this.parameterName = parameterName;
    }
    
    /**
     * Returns the name of the parameter used to build the
     * query string for the anchor that acts as the Controller
     * for opening and closing nodes.
     *
     * @return the name of the parameter
     */
    public String getParameterName()
    {
        return parameterName;
    }
    
    /**
     * Determines whether or not the root node is visible.
     *
     * @param rootVisible true if the root node of the tree
     * is to be displayed
     */
    public void setRootVisible(boolean rootVisible)
    {
        this.rootVisible = rootVisible;
    }
    
    /**
     * Returns true if the root node of the tree is displayed.
     *
     * @return true if the root node of the tree is displayed
     */
    public boolean isRootVisible()
    {
        return rootVisible;
    }
    
    /**
     * Sets the number of blank spaces around each hierarchical
     * level will be indented in relation to its parent node.
     *
     * @param indentation the number of blank spaces
     */
    public void setIndentation(int indentation)
    {
        this.indentation = indentation;
    }
    
    /**
     * Returns the number of blank spaces around each
     * hierarchical level is indented in relation to
     * its parent node.
     *
     * @return the number of blank spaces
     */
    public int getIndentation()
    {
        return indentation;
    }
    
    /**
     * Returns the name of the currently expanded node.
     *
     * @return the name of the node
     */
    public String getNodeName()
    {
        return displayNode.toString();
    }
    
    /**
     * Returns the number of rows that are currently
     * being displayed.
     *
     * @return the number of rows
     */
    public int getRowCount()
    {
        // Get level of the node to be displayed
        int rowCount = displayNode.getLevel();
    
        // Add number of children
        rowCount = rowCount + displayNode.getChildCount();
    
        // Increment by 1 if the root node is being displayed
        if (rootVisible) 
        { 
            rowCount++; 
        }
    
        // Return number of rows
        return rowCount;
    }
    
    /**
     * Sets the icon for displaying open nodes.
     *
     * @param openIcon the URI of an image file
     */
    public void setOpenIcon(String openIcon)
    {
        this.openIcon = openIcon;
    }
    
    /**
     * Sets the icon for displaying closed nodes.
     *
     * @param closedIcon the URI of an image file
     */
    public void setClosedIcon(String closedIcon)
    {
        this.closedIcon = closedIcon;
    }
    
    /**
     * Sets the icon for displaying leafs.
     *
     * @param leafIcon the URI of an image file
     */
    public void setLeafIcon(String leafIcon)
    {
        this.leafIcon = leafIcon;
    }
    
    /**
     * Workaround of a strange behaviour of Netscape and Opera browsers.
     * In these browsers the relative URL containg only the query string
     * with the path information (e.g. &quot;?path=0:1&quot;) are not
     * translated correctly into an absoulte URL. By setting a context
     * path, on which to append the query string, this behaviour should
     * be fixed.
     *
     * @param action the context path of a web resource (e.g. a JSP-File)
     */
    public void setAction(String action)
    {
        // Check property
        if (action == null) 
        { 
            action = ""; 
        }
    
        // If the specified context path already contains
        // a query string, append an ampersand character
        // for further parameter concatenation
        if (action.indexOf("=") != -1)
        {
            action = action + "&";
        }
        else
        {
            action = action + "?";
        }
    
        // Set property
        this.action = action;
    }
    
    /**
     * Constructs a valid XHTML &lt;img&gt;-tag from the specified icon.
     * Although this method uses the xml-package to generate a valid tag,
     * the xhtml-package would provide the same functionality.
     *
     * @param icon the URI of an image file
     * @return a valid XHTML &lt;img&gt;-tag
     */
    public String getImg(String icon)
    {
        // New <img>
        XML img = new XML("img",false);
    
        // Check specified icon property
        if (icon == null) 
        { 
            icon = ""; 
        }
    
        // Set src attribute
        img.addAttribute("src",icon);
    
        // return <img>
        return img.toString();
    }
    
    /**
     * Expands the tree by following the specified path
     * and returns the requested node to be displayed.
     *
     * @return the requested node to be displayed
     */
    public DefaultMutableTreeNode getRequestedNode()
    {
        // Reset node to be displayed
        displayNode = root;
    
        // Iterate trough the path array
        for (int i = 0; i < pathArray.length; i++)
        {
            if (displayNode.getDepth() > 1)
            {
                displayNode = (DefaultMutableTreeNode)
                displayNode.getChildAt(pathArray[i]);
            }
        }
    
        // Return node to be displayed
        return displayNode;
    }
    
    /**
     * Returns the hierarchical structure described in the specified root
     * node as valid XHTML. For perfomance reasons all parent nodes of the
     * requested node are collapsed. The source code does not contain any
     * formatting tags or attributes. I recommend that you use Cascading
     * Style Sheets instead. For this purpose, the tree elements are marked
     * with the class attribute values &quot;tree&quot;, &quot;parent&quot;
     * and &quot;child&quot;.
     *
     * <p>A sample Style Sheet would look like this:</p>
     *
     * <p><blockquote>
     *   <code>
     *     a { color: black; text-decoration: none; }<br />
     *     a:hover { color: black; text-decoration: underline; }<br />
     *     img { margin-right: 5px; vertical-align: middle; border: none; }<br />
     *     table { font: normal 8pt Arial,Helvetica,sans-serif; }</br />
     *     td.parent { font-weight: bold; }<br />
     *   </code>
     * </blockquote></p>
     *
     * @return the hierarchical structure within a &lt;table&gt;-tag
     */
    public String getHtml()
    {
        // Expand the tree
        displayNode = getRequestedNode();
    
        // New <table>
        Table table = new Table();
        table.setClass("tree");
    
        // Reset auto indentation
        int autoIndentation = 0;
    
        // Initialize ancestor node with the first child of
        // the node to be displayed (for parent recursion)
        TreeNode ancestor = displayNode.getFirstChild();
    
        // Read all ancestors into an array
        ArrayList list = new ArrayList();
        while ((ancestor = ancestor.getParent()) != null)
        {
            list.add(ancestor);
        }
    
        // For each ancestor (ordered desc beginning at the root)
        for (int i = list.size(); i > 0; i--)
        {
            // Get current ancestor
            DefaultMutableTreeNode parent =
                (DefaultMutableTreeNode)list.get(i-1);
        
            // Displays the ancestor if it's not the root
            // or the rootVisible property is set to true
            if (!parent.isRoot() || rootVisible)
            {
                // New <td>
                TD td = new TD();
                td.setClass("parent");
        
                // Generate href for this ancestor (the
                // level is decreased by one to provide
                // an anchor to its parent node)
                String href = action + parameterName
                    + "=" + getPath(parent.getLevel()-1);
        
                // Add indentation to <td>
                for (int j = 0; j < autoIndentation; j++)
                {
                    td.addElement("&nbsp;");
                }
        
                // Add icon with <a> to <td>
                td.addElement(new A(href,getImg(openIcon)));
        
                // Add ancestor with <nobr> to <td>
                td.addElement(new NOBR(parent.toString()));
        
                // Add <td> to <tr> to <table>
                table.addElement(new TR(td));
        
                // Increment auto indentation
                autoIndentation = autoIndentation + indentation;
            }
        }
    
        // For each child
        for (int i = 0; i < displayNode.getChildCount(); i++)
        {
            // New <td>
            TD td = new TD();
            td.setClass("child");
        
            // Get current child
            DefaultMutableTreeNode child =
                (DefaultMutableTreeNode)displayNode
                .getChildAt(i);
        
            // Generate path == path + child index
            String autoPath = getPath(child
                .getLevel()-1) + ":" + i;
        
            // Trim leading colon
            if (autoPath.startsWith(":"))
            {
                autoPath = autoPath.substring(1);
            }
        
            // Generate href for this child
            String href = action + parameterName
                + "=" + autoPath;
        
            // Add indentation to <td>
            for (int j = 0; j < autoIndentation; j++)
            {
                td.addElement("&nbsp;");
            }
        
            // New <img> with default leaf icon
            String img = getImg(leafIcon);
        
            // Set closed node icon, if child is not a leaf
            if (!child.isLeaf()) 
            { 
                img = getImg(closedIcon); 
            }
        
            // Add icon with <a> (not for leafs) to <td>
            if (!child.isLeaf()) 
            {
                td.addElement(new A(href,img)); 
            }
        
            // Add icon without <a> (leafs only) to <td>
            if (child.isLeaf()) 
            { 
                td.addElement(img); 
            }
        
            // Add child with <nobr> to <td>
            td.addElement(new NOBR(child.toString()));
        
            // Add <td> to <tr> to <table>
            table.addElement(new TR(td));
        }
    
        // Return <table>
        return table.toString();
    }
    
    /**
     * Overrides <tt>toString()</tt> to print something meaningful.
     * Returns the hierarchical structure described in the specified
     * root node as valid XHTML.
     *
     * @see #getHtml
     */
    public String toString()
    {
        return getHtml();
    }
    
    /**
     * Creates and returns a sample tree model. Used primarily
     * during the design of the JavaServerPage to show something
     * interesting.
     *
     * @return a DefaultMutableTreeNode with a sample tree model
     */
    protected static DefaultMutableTreeNode getDefaultTreeModel()
    {
        // New root node
        DefaultMutableTreeNode root =
        new DefaultMutableTreeNode("Root");
    
        // First level
        for (int i = 1; i <= 5; i++)
        {
            // New node
            DefaultMutableTreeNode folder =
                new DefaultMutableTreeNode("Folder-" + i);
        
            // Add node to root
            root.add(folder);
        
            // Second level
            for (int j = 1; j <= 3; j++)
            {
                // New node
                DefaultMutableTreeNode subfolder =
                new DefaultMutableTreeNode("Subfolder-" + j);
        
                // Add node to parent node
                folder.add(subfolder);
        
                // Third level
                for (int k = 1; k <= 3; k++)
                {
                    // New anchor
                    A a = new A("http://jakarta.apache.org");
                    a.setTarget("target").addElement("Document-" + k);
            
                    // New node (leaf)
                    DefaultMutableTreeNode document =
                        new DefaultMutableTreeNode(a.toString());
            
                    // Add node to parent node
                    subfolder.add(document);
                }
            }
        }
    
        // Return root node
        return root;
    }
    
    /**
     * Returns the specified string encoded into a format suitable for
     * HTML. All single-quote, double-quote, greater-than, less-than
     * and ampersand characters are replaced with their corresponding
     * HTML Character Entity codes. You can use this method to encode
     * the designated node names before appending this node to your
     * tree model. Please don't encode node names that already include
     * a valid tag, because they are equally converted and thus won't
     * be displayed by the browser.
     *
     * @param in the String to encode
     * @return the encoded String
     */
    public static String encodeToHtml(String in)
    {
        // New StringBuffer for output concatenation
        StringBuffer out = new StringBuffer();
    
        // For each character in the input string
        for (int i = 0; in != null && i < in.length(); i++)
        {
            // Get the current character
            char c = in.charAt(i);
        
            // Encode this character
            if (c == '\'')
            {
                out.append("&#039;");
            }
            else if (c == '\"')
            {
                out.append("&quot;");
            }
            else if (c == '<')
            {
                out.append("&lt;");
            }
            else if (c == '>')
            {
                out.append("&gt;");
            }
            else if (c == '&')
            {
                out.append("&amp;");
            }
            else
            {
                out.append(c);
            }
        }
    
        // Return encoded string
        return out.toString();
    }
}
