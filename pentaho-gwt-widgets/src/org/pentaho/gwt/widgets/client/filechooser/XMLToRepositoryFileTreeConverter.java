package org.pentaho.gwt.widgets.client.filechooser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;


  /**
   * @author rmansoor
   *
   */
  public class XMLToRepositoryFileTreeConverter {
    String xmlText;

    public RepositoryFileTree getRepositoryFileTree(Element element) {
      RepositoryFileTree fileTree = new RepositoryFileTree();
      fileTree.setFile(getRepositoryFileFromXmlElement(getFileNode(element)));
      List<RepositoryFileTree> children = new ArrayList<RepositoryFileTree>();
      List<Node> childrenNodes = getChildrenNodes(element);
      if (childrenNodes != null && childrenNodes.size() > 0) {
          for (int i=0; i < childrenNodes.size(); i++) {
            children.add(getRepositoryFileTree((Element)childrenNodes.get(i)));
          }
      }
      fileTree.setChildren(children);

      return fileTree;
    }
    
    public static final List<RepositoryFile> getTrashFiles(String xmlData) {
      return getFileListFromXml(xmlData);
    }
    
    public static final List<RepositoryFile> getFileListFromXml(String xmlData) {
      Document doc = getXMLDocumentFromString(xmlData);
      List<RepositoryFile> files = new ArrayList<RepositoryFile>();
      NodeList nodes = doc.getElementsByTagName("repositoryFileDto");
      for (int i=0; i<nodes.getLength(); i++) {
        RepositoryFile trashItem = getRepositoryFileFromXmlElement(nodes.item(i));
        files.add(trashItem);
      }
      return files;
    }
    
  
    private static final String getName(Element repoFile) {
      return getNodeValueByTagName(repoFile, "name");
    }
    
    private static final String getId(Element repoFile) {
      return getNodeValueByTagName(repoFile, "id");
    }
    
    private static final String getCreatedDate(Element repoFile) {
      return getNodeValueByTagName(repoFile, "createdDate");
    }
    
    private static final String getlastModifiedDate(Element repoFile) {
      return getNodeValueByTagName(repoFile, "lastModifiedDate");
    }

    private static final String getFileSize(Element repoFile) {
      return getNodeValueByTagName(repoFile, "fileSize");
    }
    
    private static final String isFolder(Element repoFile) {
      return getNodeValueByTagName(repoFile, "folder");
    }
    
    private static final String getPath(Element repoFile) {
      return getNodeValueByTagName(repoFile, "path");
    }

    private static final String isHidden(Element repoFile) {
      return getNodeValueByTagName(repoFile, "hidden");
    }

    private static final String isVersioned(Element repoFile) {
      return getNodeValueByTagName(repoFile, "versioned");      
    }

    private static final String getVersionId(Element repoFile) {
      return getNodeValueByTagName(repoFile, "versionId");      
    }

    private static final String isLocked(Element repoFile) {
      return getNodeValueByTagName(repoFile, "locked");
    }

    private static final String getLockOwner(Element repoFile) {
      return getNodeValueByTagName(repoFile, "lockOwner");      
    }

    private static final String getLockMessage(Element repoFile) {
      return getNodeValueByTagName(repoFile, "lockMessage");      
    }

    private static final String getLockDate(Element repoFile) {
      return getNodeValueByTagName(repoFile, "lockDate");
    }

    private static final String getOwner(Element repoFile) {
      return getNodeValueByTagName(repoFile, "owner");
    }

    private static final String getOwnerType(Element repoFile) { 
      return getNodeValueByTagName(repoFile, "ownerType");
    }
    
    private static final String getTitle(Element repoFile) {
      return getNodeValueByTagName(repoFile, "title");
    }

    private static final String getDescription(Element repoFile) { 
      return getNodeValueByTagName(repoFile, "description");
    }

    private static final String getLocale(Element repoFile) {
      return getNodeValueByTagName(repoFile, "locale");
    }

    private static final String getOriginalParentFolderPath(Element repoFile) { 
      return getNodeValueByTagName(repoFile, "originalParentFolderPath");
    }

    private static final String getOriginalParentFolderId(Element repoFile) {
      return getNodeValueByTagName(repoFile, "originalParentFolderId");
    }

    private static final String getDeletedDate(Element repoFile) {
      return getNodeValueByTagName(repoFile, "deletedDate");
    }

    private static final Date parseDateTime(String dateTimeString) { 
      // parse the date
      Date date = null;
        try {
          Long timeInMillis = Long.parseLong(dateTimeString);
          date = new Date(timeInMillis);
        } catch (Exception e) {
          return null;
        }
      return date;     
    }
    
    private static RepositoryFile getRepositoryFileFromXmlElement(Node repoFileNode) {
      Element repoFileElement = (Element) repoFileNode;
      RepositoryFile repoFile = new RepositoryFile();
      repoFile.setName(getName(repoFileElement));
      repoFile.setId(getId(repoFileElement));
      repoFile.setCreatedDate(parseDateTime(getCreatedDate(repoFileElement)));
      repoFile.setLastModifiedDate(parseDateTime(getlastModifiedDate(repoFileElement)));
      repoFile.setFileSize(Long.parseLong(getFileSize(repoFileElement)));
      repoFile.setFolder(Boolean.parseBoolean(isFolder(repoFileElement)));
      repoFile.setPath(getPath(repoFileElement));
      repoFile.setHidden(Boolean.parseBoolean(isHidden(repoFileElement)));
      repoFile.setVersioned(Boolean.parseBoolean(isVersioned(repoFileElement)));
      repoFile.setVersionId(getVersionId(repoFileElement));
      repoFile.setLocked(Boolean.parseBoolean(isLocked(repoFileElement)));
      repoFile.setLockOwner(getLockOwner(repoFileElement));
      repoFile.setLockMessage(getLockMessage(repoFileElement));
      repoFile.setLockDate(parseDateTime(getLockDate(repoFileElement)));
      repoFile.setOwner(getOwner(repoFileElement));
      repoFile.setOwnerType(Integer.parseInt(getOwnerType(repoFileElement)));
      repoFile.setTitle(getTitle(repoFileElement));
      repoFile.setDescription(getDescription(repoFileElement));
      repoFile.setLocale(getLocale(repoFileElement));
      repoFile.setOriginalParentFolderPath(getOriginalParentFolderPath(repoFileElement));
      repoFile.setOriginalParentFolderId(getOriginalParentFolderId(repoFileElement));
      repoFile.setDeletedDate(parseDateTime(getDeletedDate(repoFileElement)));
      
      return repoFile;
    }

    
    /**
     * @param String
     */
    public XMLToRepositoryFileTreeConverter(String xmlText) {
      super();
      System.out.println(xmlText);
      this.xmlText = xmlText;
    }
    /**
     * @return
     */
    public RepositoryFileTree getTree() {
      Document tempObj = getXMLDocumentFromString(xmlText);
      RepositoryFileTree value = getRepositoryFileTree(tempObj.getDocumentElement());
      return value;
    }
    
    private static Document getXMLDocumentFromString(String xmlText) {
      return (Document) XMLParser.parse(xmlText);
    }

    /*
     * Get Node Value of the element matching the tag name
     */
    private static final String getNodeValueByTagName(Element element, String tagName) {
      Node node = getNodeByTagName(element, tagName);
      if(node != null && node.getFirstChild() != null) {
        return node.getFirstChild().getNodeValue();
      } else {
        return null;
      }
    }
    
    private static final Node getFileNode(Element element) {
      return getNodeByTagName(element, "file");
    }
    
    private static final List<Node> getChildrenNodes(Element element) {
      return getNodesByTagName(element, "children");
    }
    
    /*
     * Return the first name that matched the tagName. Starting from the 
     * current element location
     */
    private static final Node getNodeByTagName(Element element, String tagName) {
      NodeList list = element.getChildNodes();
      for(int i=0;i<list.getLength();i++) {
        Node node = list.item(i);
        if( node != null && node.getNodeName().equals(tagName)) {
          return node;
        }
      }
      return null;
    }
    
    /*
     * Return all node matching the tagName, starting from the current element
     * location
     */
    private static final List<Node> getNodesByTagName(Element element, String tagName) {
      List<Node> nodes = new ArrayList<Node>();
      NodeList list = element.getChildNodes();
      for(int i=0;i<list.getLength();i++) {
        Node node = list.item(i);
        if( node != null && node.getNodeName().equals(tagName)) {
          nodes.add(node);
        }
      }
      return nodes;
    }

}