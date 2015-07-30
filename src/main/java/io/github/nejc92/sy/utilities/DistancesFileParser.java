package io.github.nejc92.sy.utilities;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DistancesFileParser extends DefaultHandler {

    private final String fileName;
    private List<List<Integer>> greaterPositionsPairs;
    private List<Integer> greaterPositionPair;
    private boolean isDistance;

    public DistancesFileParser(String fileName) {
        this.fileName = fileName;
    }

    public List<List<Integer>> getParsedData() {
        SAXParser parser = createSaxParser();
        tryToParseDataToList(parser);
        return greaterPositionsPairs;
    }

    private SAXParser createSaxParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        return tryToCreateSaxParser(factory);
    }

    private SAXParser tryToCreateSaxParser(SAXParserFactory factory) {
        try {
            return factory.newSAXParser();
        } catch (SAXException | ParserConfigurationException e) {
            throw new IllegalStateException("Error: " + e.getMessage(), e);
        }
    }

    private void tryToParseDataToList(SAXParser parser) {
        try {
            parser.parse(fileName, this);
        } catch (IOException | SAXException e) {
            throw new IllegalStateException("Error: " + e.getMessage(), e);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        this.greaterPositionsPairs = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String elementName, Attributes attributes)
            throws SAXException {
        if (elementName.equalsIgnoreCase("from"))
            greaterPositionPair = new ArrayList<>();
        else if (elementName.equalsIgnoreCase("distance"))
            isDistance = true;
    }

    @Override
    public void endElement(String uri, String localName, String elementName) throws SAXException {
        if (elementName.equalsIgnoreCase("from"))
            greaterPositionsPairs.add(greaterPositionPair);
    }

    @Override
    public void characters(char[] character, int start, int length) throws SAXException {
        if (isDistance) {
            String stringData = new String(character, start, length);
            int data = Integer.parseInt(stringData);
            greaterPositionPair.add(data);
            isDistance = false;
        }
    }
}