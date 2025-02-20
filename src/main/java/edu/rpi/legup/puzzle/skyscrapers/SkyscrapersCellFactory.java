package edu.rpi.legup.puzzle.skyscrapers;

import edu.rpi.legup.model.gameboard.Board;
import edu.rpi.legup.model.gameboard.ElementFactory;
import edu.rpi.legup.model.gameboard.PuzzleElement;
import edu.rpi.legup.save.InvalidFileFormatException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.awt.*;

public class SkyscrapersCellFactory extends ElementFactory {
    /**
     * Creates a puzzleElement based on the xml document Node and adds it to the board
     *
     * @param node  node that represents the puzzleElement
     * @param board board to add the newly created cell
     * @return newly created cell from the xml document Node
     * @throws InvalidFileFormatException if input is invalid
     */
    @Override
    public PuzzleElement importCell(Node node, Board board) throws InvalidFileFormatException {
        try {
            //SkyscrapersBoard treeTentBoard = (SkyscrapersBoard) board;
            SkyscrapersBoard skyscrapersBoard = (SkyscrapersBoard) board;
            int width = skyscrapersBoard.getWidth();
            int height = skyscrapersBoard.getHeight();
            NamedNodeMap attributeList = node.getAttributes();
            if (node.getNodeName().equalsIgnoreCase("cell")) {

                int value = Integer.valueOf(attributeList.getNamedItem("value").getNodeValue());
                int x = Integer.valueOf(attributeList.getNamedItem("x").getNodeValue());
                int y = Integer.valueOf(attributeList.getNamedItem("y").getNodeValue());
                if (x >= width || y >= height) {
                    throw new InvalidFileFormatException("TreeTent Factory: cell location out of bounds");
                }
                if (value < 0 || value > 3) {
                    throw new InvalidFileFormatException("TreeTent Factory: cell unknown value");
                }

                SkyscrapersCell cell = new SkyscrapersCell(SkyscrapersType.convertToSkyType(value), new Point(x, y), width);
                cell.setIndex(y * height + x);
                return cell;
            } else {
                if (node.getNodeName().equalsIgnoreCase("line")) {
                    int x1 = Integer.valueOf(attributeList.getNamedItem("x1").getNodeValue());
                    int y1 = Integer.valueOf(attributeList.getNamedItem("y1").getNodeValue());
                    int x2 = Integer.valueOf(attributeList.getNamedItem("x2").getNodeValue());
                    int y2 = Integer.valueOf(attributeList.getNamedItem("y2").getNodeValue());
                    if (x1 >= width || y1 >= height || x2 >= width || y2 >= height) {
                        throw new InvalidFileFormatException("TreeTent Factory: line location out of bounds");
                    }

                    SkyscrapersCell c1 = skyscrapersBoard.getCell(x1, y1);
                    SkyscrapersCell c2 = skyscrapersBoard.getCell(x2, y2);
                    return new SkyscrapersLine(c1, c2);
                } else {
                    throw new InvalidFileFormatException("TreeTent Factory: unknown puzzleElement puzzleElement");
                }
            }
        } catch (NumberFormatException e) {
            throw new InvalidFileFormatException("TreeTent Factory: unknown value where integer expected");
        } catch (NullPointerException e) {
            throw new InvalidFileFormatException("TreeTent Factory: could not find attribute(s)");
        }
    }

    /**
     * Creates a xml document puzzleElement from a cell for exporting
     *
     * @param document      xml document
     * @param puzzleElement PuzzleElement cell
     * @return xml PuzzleElement
     */
    public org.w3c.dom.Element exportCell(Document document, PuzzleElement puzzleElement) {
        if (puzzleElement instanceof SkyscrapersCell) {
            org.w3c.dom.Element cellElement = document.createElement("cell");

            SkyscrapersCell cell = (SkyscrapersCell) puzzleElement;
            Point loc = cell.getLocation();

            cellElement.setAttribute("value", String.valueOf(cell.getData()));
            cellElement.setAttribute("x", String.valueOf(loc.x));
            cellElement.setAttribute("y", String.valueOf(loc.y));

            return cellElement;
        } else {
            org.w3c.dom.Element lineElement = document.createElement("line");

            SkyscrapersLine line = (SkyscrapersLine) puzzleElement;

            lineElement.setAttribute("x1", String.valueOf(line.getC1().getLocation().x));
            lineElement.setAttribute("y1", String.valueOf(line.getC1().getLocation().y));
            lineElement.setAttribute("x2", String.valueOf(line.getC2().getLocation().x));
            lineElement.setAttribute("y2", String.valueOf(line.getC2().getLocation().y));

            return lineElement;
        }
    }
}
