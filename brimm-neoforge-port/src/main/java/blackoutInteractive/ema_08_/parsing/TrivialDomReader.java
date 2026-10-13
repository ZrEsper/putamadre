package blackoutInteractive.ema_08_.parsing;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class TrivialDomReader {
    private final Document doc;
    private final String root;
    private final String[] rootTagPath;
    private final String loadpath;

    private String formatTagPath(String[] tagpath) {
        return "/" + String.join((CharSequence)" : ", this.validateAndFormatTagpath(tagpath));
    }

    private String[] validateAndFormatTagpath(String[] tagpath) {
        if (tagpath == null) {
            throw new IllegalArgumentException("The tagpath cannot be null");
        }
        if (tagpath.length == 0) {
            return this.rootTagPath;
        }
        if (tagpath[0].equals(this.root)) {
            return tagpath;
        }
        String[] rootedTagPath = new String[tagpath.length + 1];
        System.arraycopy(tagpath, 0, rootedTagPath, 1, tagpath.length);
        rootedTagPath[0] = this.root;
        return rootedTagPath;
    }

    private Element getElementByPath(String[] tagpath) {
        tagpath = this.validateAndFormatTagpath(tagpath);
        Element current = this.doc.getDocumentElement();
        for (int i = 1; i < tagpath.length; ++i) {
            NodeList children = current.getChildNodes();
            boolean found = false;
            for (int j = 0; j < children.getLength(); ++j) {
                Node child = children.item(j);
                if (child.getNodeType() != 1 || !child.getNodeName().equals(tagpath[i])) continue;
                current = (Element)child;
                found = true;
                break;
            }
            if (found) continue;
            return null;
        }
        return current;
    }

    public TrivialDomReader(File file) throws ParserConfigurationException, SAXException, IOException {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("Invalid XML file");
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(false);
        factory.setIgnoringComments(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.doc = builder.parse(file);
        this.doc.getDocumentElement().normalize();
        this.root = this.doc.getDocumentElement().getTagName();
        this.rootTagPath = new String[]{this.root};
        this.loadpath = file.getAbsolutePath();
    }

    public String getLoadedFilePath() {
        return this.loadpath;
    }

    public String getRootTag() {
        return this.root;
    }

    public boolean elementExists(String[] tagpath) {
        return this.getElementByPath(tagpath) != null;
    }

    public String getElementValue(String[] tagpath) {
        Element elem = this.getElementByPath(tagpath);
        if (elem == null) {
            throw new NoSuchElementException("No element was found at " + this.formatTagPath(tagpath));
        }
        return elem.getTextContent();
    }

    public boolean hasAttribute(String attribute, String[] tagpath) {
        Element elem = this.getElementByPath(tagpath);
        if (elem == null) {
            throw new NoSuchElementException("No element was found at " + this.formatTagPath(tagpath));
        }
        return elem.hasAttribute(attribute);
    }

    public String getAttributeValue(String attribute, String[] tagpath) {
        Element elem = this.getElementByPath(tagpath);
        if (elem == null) {
            throw new NoSuchElementException("No element was found at " + this.formatTagPath(tagpath));
        }
        if (!elem.hasAttribute(attribute)) {
            throw new NoSuchElementException("Element at " + this.formatTagPath(tagpath) + " has no such attribute as " + attribute);
        }
        return elem.getAttribute(attribute);
    }

    public <T> T getElementValue(String[] tagpath, Function<String, T> transformer) {
        return Objects.requireNonNull(transformer, "Cannot use null transformer").apply(this.getElementValue(tagpath));
    }

    public double getElementValueCastDouble(String[] tagpath) {
        return Double.parseDouble(this.getElementValue(tagpath));
    }

    public int getElementValueCastInt(String[] tagpath) {
        return Integer.parseInt(this.getElementValue(tagpath));
    }
}

