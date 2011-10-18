package org.pentaho.gwt.widgets.client.filechooser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.NodeList;


  /**
   * @author rmansoor
   *
   */
  public class XMLToRepositoryFileTreeConverter {
    String xmlText;
    String dateFormat = "MM-dd-yyyy";

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
    
    public List<RepositoryFile> getTrashFiles(String xmlData) {
      Document doc = getXMLDocumentFromString(xmlData);
      List<RepositoryFile> trashFiles = new ArrayList<RepositoryFile>();
      NodeList nodes = doc.getElementsByTagName("repositoryFileDto");
      for (int i=0; i<nodes.getLength(); i++) {
        RepositoryFile trashItem = getRepositoryFileFromXmlElement(nodes.item(i));
        trashFiles.add(trashItem);
      }
      return trashFiles;
    }
    
  
    private final String getName(Element repoFile) {
      return getNodeValueByTagName(repoFile, "name");
    }
    
    private final String getId(Element repoFile) {
      return getNodeValueByTagName(repoFile, "id");
    }
    
    private final String getCreatedDate(Element repoFile) {
      return getNodeValueByTagName(repoFile, "createdDate");
    }
    
    private final String getlastModifiedDate(Element repoFile) {
      return getNodeValueByTagName(repoFile, "lastModifiedDate");
    }

    private final String getFileSize(Element repoFile) {
      return getNodeValueByTagName(repoFile, "fileSize");
    }
    
    private final String isFolder(Element repoFile) {
      return getNodeValueByTagName(repoFile, "folder");
    }
    
    private final String getPath(Element repoFile) {
      return getNodeValueByTagName(repoFile, "path");
    }

    private final String isHidden(Element repoFile) {
      return getNodeValueByTagName(repoFile, "hidden");
    }

    private final String isVersioned(Element repoFile) {
      return getNodeValueByTagName(repoFile, "versioned");      
    }

    private final String getVersionId(Element repoFile) {
      return getNodeValueByTagName(repoFile, "versionId");      
    }

    private final String isLocked(Element repoFile) {
      return getNodeValueByTagName(repoFile, "locked");
    }

    private final String getLockOwner(Element repoFile) {
      return getNodeValueByTagName(repoFile, "lockOwner");      
    }

    private final String getLockMessage(Element repoFile) {
      return getNodeValueByTagName(repoFile, "lockMessage");      
    }

    private final String getLockDate(Element repoFile) {
      return getNodeValueByTagName(repoFile, "lockDate");
    }

    private final String getOwner(Element repoFile) {
      return getNodeValueByTagName(repoFile, "owner");
    }

    private final String getOwnerType(Element repoFile) { 
      return getNodeValueByTagName(repoFile, "ownerType");
    }
    
    private final String getTitle(Element repoFile) {
      return getNodeValueByTagName(repoFile, "title");
    }

    private final String getDescription(Element repoFile) { 
      return getNodeValueByTagName(repoFile, "description");
    }

    private final String getLocale(Element repoFile) {
      return getNodeValueByTagName(repoFile, "locale");
    }

    private final String getOriginalParentFolderPath(Element repoFile) { 
      return getNodeValueByTagName(repoFile, "originalParentFolderPath");
    }

    private final String getOriginalParentFolderId(Element repoFile) {
      return getNodeValueByTagName(repoFile, "originalParentFolderId");
    }

    private final String getDeletedDate(Element repoFile) {
      return getNodeValueByTagName(repoFile, "deletedDate");
    }

    private Date parseDateTime(String dateTimeString) { 
      // parse the date
      Date date = null;
        try {
          date = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm:ss").parse(dateTimeString);
        } catch (Exception e) {
          return null;
        }
      return date;     
    }
    
    private RepositoryFile getRepositoryFileFromXmlElement(Node repoFileNode) {
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
    
    private Document getXMLDocumentFromString(String xmlText) {
      return (Document) XMLParser.parse(xmlText);
    }

    /*
     * Get Node Value of the element matching the tag name
     */
    private String getNodeValueByTagName(Element element, String tagName) {
      Node node = getNodeByTagName(element, tagName);
      if(node != null && node.getFirstChild() != null) {
        return node.getFirstChild().getNodeValue();
      } else {
        return null;
      }
    }
    private Node getFileNode(Element element) {
      return getNodeByTagName(element, "file");
    }
    
    private List<Node> getChildrenNodes(Element element) {
      return getNodesByTagName(element, "children");
    }
    
    /*
     * Return the first name that matched the tagName. Starting from the 
     * current element location
     */
    private Node getNodeByTagName(Element element, String tagName) {
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
    private List<Node> getNodesByTagName(Element element, String tagName) {
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