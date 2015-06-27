package io.github.nejc92.sy;

import io.github.nejc92.sy.Game.Action;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardFileParser extends DefaultHandler {

    private final String fileName;
    private List<List<Action>> boardPositions;
    private List<Action> temporaryPosition;
    private Action temporaryAction;
    private boolean isTransportation;
    private boolean isDestination;

    public BoardFileParser(String fileName) {
        this.fileName = fileName;
    }

    public List<List<Action>> getParsedData() {
        SAXParser parser = createSaxParser();
        tryToParseDataToList(parser);
        return boardPositions;
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
        this.boardPositions = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String elementName, Attributes attributes)
            throws SAXException {
        if (elementName.equalsIgnoreCase("boardPosition"))
            temporaryPosition = new ArrayList<>();
        else if (elementName.equalsIgnoreCase("action"))
            temporaryAction = new Action();
        else if (elementName.equalsIgnoreCase("transportation"))
            isTransportation = true;
        else if (elementName.equalsIgnoreCase("destination"))
            isDestination = true;
    }

    @Override
    public void endElement(String uri, String localName, String elementName) throws SAXException {
        if (elementName.equalsIgnoreCase("boardPosition"))
            boardPositions.add(temporaryPosition);
        else if (elementName.equalsIgnoreCase("action"))
            temporaryPosition.add(temporaryAction);
    }

    @Override
    public void characters(char[] character, int start, int length) throws SAXException {
        if (isTransportation) {
            temporaryAction.transportation = charactersToTransportation(character, start, length);
            isTransportation = false;
        }
        else if (isDestination) {
            temporaryAction.destination = charactersToDestination(character, start, length);
            isDestination = false;
        }
    }

    private Action.Transportation charactersToTransportation(char[] characters, int start, int length) {
        String dataString = new String(characters, start, length);
        return tryToCreateTransportationFromString(dataString);
    }

    private Action.Transportation tryToCreateTransportationFromString(String string) {
        switch (string) {
            case "taxi":
                return Action.Transportation.TAXI;
            case "bus":
                return Action.Transportation.BUS;
            case "underground":
                return Action.Transportation.UNDERGROUND;
            case "boat":
                return Action.Transportation.BOAT;
            default:
                throw new IllegalStateException("Error: invalid transportation data in board file.");
        }
    }

    private int charactersToDestination(char[] characters, int start, int length) {
        String dataString = new String(characters, start, length);
        return tryToCreateDestinationFromString(dataString);
    }

    private int tryToCreateDestinationFromString(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Error: invalid destination data in board file.");
        }
    }
}