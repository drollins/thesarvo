package com.thesarvo.guide.client.view.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.xml.client.Node;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.thesarvo.guide.client.Thesarvoguide2;
import com.thesarvo.guide.client.controller.Controller;
import com.thesarvo.guide.client.geo.CoordinateConversion;
import com.thesarvo.guide.client.geo.CoordinateConversion.UTM;
import com.thesarvo.guide.client.geo.GeoUtil;
import com.thesarvo.guide.client.geo.Point;
import com.thesarvo.guide.client.util.BrowserUtil;
import com.thesarvo.guide.client.util.StringUtil;
import static com.thesarvo.guide.client.util.StringUtil.*;
import com.thesarvo.guide.client.xml.XPath;
import com.thesarvo.guide.client.xml.XmlSimpleModel;

public class GpsReadNode extends ReadNode
{
	
	private static final String LONGITUDE = "@longitude";

	private static final String LATITUDE = "@latitude";

	private static final String HEIGHT = "@height";

	private static final String NORTHING = "@northing";

	private static final String EASTING = "@easting";

	private static final String ZONE = "@zone";

	private static final String DESCRIPTION = "@description";

	private static final String CODE = "@code";

	private static final double LN2 = Math.log(2);

	//@UiField
	//PagingScrollTable<Point> table;
	
	//@UiField
	//Anchor googleMapsAnchor;
	
	@UiField
	Anchor googleEarthAnchor;
	
	@UiField
	MapPanel mapPanel;
	
	//@UiField
	//FlexTable gpsTable;
	
	@UiField(provided=true)
	CellTable<XmlSimpleModel> cellTable;
	
	@UiField
	Button mapButton2;
	
	@UiField
	DivElement linksDiv;

	ListDataProvider<XmlSimpleModel> dataProvider = new ListDataProvider<XmlSimpleModel>();
	
	interface MyUiBinder extends UiBinder<Widget, GpsReadNode> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	//private String kmlUrl;
	
	//NativeMap nativeMap;

	private List<Node> nodes;

	public GpsReadNode()
	{
			
	}

	@Override
	public void init()
	{

			
		cellTable = setupTable(getModel(), dataProvider);
	    initTableCols(cellTable, false);
	    
	    

		
		
		initWidget(uiBinder.createAndBindUi(this));
			
//	    List<Point> points = new ArrayList<Point>();
//	    points.add(new Point("test","test2",1,2,3));
//	    points.add(new Point("test","test2",1,2,3));
//	    
//	    DefaultTableModel<Point> tableModel = new DefaultTableModel<Point>(points);
//
//
//	    table.setTableModel(tableModel);
//	    table.gotoFirstPage();
//	    table.getHeaderTable().setHeight("20px");
	    //table.getElement().getStyle().setOverflow(Overflow.VISIBLE);
	    //table.getHeaderTable().getElement().getStyle().setOverflow(Overflow.VISIBLE);
	    //table.getDataTable().getElement().getStyle().setOverflow(Overflow.VISIBLE);
	    
		super.init();
		
		nodes = XPath.selectNodes(((XmlSimpleModel)getModel()).getXml(), "point");
		
		//String baseUrl = "../../download/resources/confluence.extra.guide:guide/thesarvomaps.html?url=";
		
		//String kmlUrl = Controller.get().getServletUrl();
		
		// TODO - this has to be an absolute URL to work with good maps, get rid of hard codedness
		String kmlUrl = "http://www.thesarvo.com/confluence/plugins/servlet/ts.kml?pageId=" + Controller.get().getGuideId();
		
		/*
		baseUrl += URL.encodeComponent(kmlUrl);
		
		String zoom = "16";
		if (nodes!=null && nodes.size() > 10)
			zoom = "12";
		if (nodes!=null && nodes.size() > 30)
			zoom = "8";
		
		baseUrl += "&zoom=" + zoom;
		*/
		//googleMapsAnchor.setHref(baseUrl);
		
		googleEarthAnchor.setHref("../../plugins/servlet/ts.kml?pageId=" + Controller.get().getGuideId() );
		
		//setupMapsPanel(nodes);
		
		/*
		if (BrowserUtil.isMobileBrowser() && PhoneGap.isAvailable() && NativeMap.isAvailable() )
		{
			mapButton2.setVisible( true );
			mapButton2.addClickHandler(new MapButtonClickHandler());
		}
		else
		*/
		
		mapButton2.setVisible( false );
		
		
		
		//if (! Thesarvoguide2.isGWTDevMode())
		{
		
			mapPanel.setKmlUrl(kmlUrl);
			//mapPanel.setPoints(nodes);
			mapPanel.init(nodes);
		}

	}
	
	@Override
	public void setWidgetValuesFromModel()
	{
		
		super.setWidgetValuesFromModel();
		updateDataProvider(model, dataProvider);   
		
	}

	public static CellTable<XmlSimpleModel> setupTable(XmlSimpleModel model, ListDataProvider<XmlSimpleModel> dataProvider) 
	{
		CellTable<XmlSimpleModel> cellTable = new CellTable<XmlSimpleModel>();
		updateDataProvider(model, dataProvider);    
	    
	    cellTable.setWidth("100%", true);
	    dataProvider.addDataDisplay(cellTable);
	    return cellTable;
	}

	public static void updateDataProvider(XmlSimpleModel model,
			ListDataProvider<XmlSimpleModel> dataProvider)
	{
		List<XmlSimpleModel> data = model.getList("point");		    
	    
		normalizeData(data);
		
		dataProvider.setList(data);
	    dataProvider.refresh();
	}
	
	public static void normalizeData(List<XmlSimpleModel> data)
	{
		for (XmlSimpleModel xsm : data)
		{
			String east = xsm.get(EASTING);
			String north = xsm.get(NORTHING);
			String zone = xsm.get(ZONE);
			String lat = xsm.get(LATITUDE);
			String lon = xsm.get(LONGITUDE);
			
			if (isNotEmpty(east) 
					&& isNotEmpty(north) )
			{
				if (isEmpty(zone))
				{
					zone = "55G"; // tassie zone
					xsm.put(ZONE, zone);
				}
				
				if (isEmpty(lat)
					|| isEmpty(lon)	)
				{
					// calculate the lat/lon
					
					try
					{
						CoordinateConversion cc = new CoordinateConversion();
						
						double[] latlon = GeoUtil.getLatLong(east, north, zone);
						if (latlon != null)
						{
							xsm.put(LATITUDE, GeoUtil.formatLatLong(latlon[0]));
							xsm.put(LONGITUDE, GeoUtil.formatLatLong(latlon[1]));
						}
					}
					catch (Exception e)
					{
						
					}
				}
				
			}
			else
			{
				// UTM is (partially) empty - calculate it
				if (isNotEmpty(lat)
						&& isNotEmpty(lon))
				{
					UTM utm = GeoUtil.getUTMFromLatLon(lat, lon);
					if (utm != null)
					{
						xsm.put(EASTING, "" + (int) utm.getEasting() );
						xsm.put(NORTHING, "" + (int) utm.getNorthing() );
						xsm.put(ZONE, "" + utm.getLongZone() + utm.getLatZone() );
					}
				}
			}
					
		}
		
	}

	public static void initTableCols(CellTable<XmlSimpleModel> cellTable2, boolean editable)
	{
	    Column<XmlSimpleModel, String> codeColumn = new BoundColumn(editable, CODE);
		cellTable2.setColumnWidth(codeColumn, 10, Unit.PCT);
		cellTable2.addColumn(codeColumn, "Code");

	    Column<XmlSimpleModel, String> descColumn = new BoundColumn(editable, DESCRIPTION);
		cellTable2.setColumnWidth(descColumn, 30, Unit.PCT);
		cellTable2.addColumn(descColumn, "Description");

	    Column<XmlSimpleModel, String> zoneColumn = new BoundColumn(editable, ZONE);
		cellTable2.setColumnWidth(zoneColumn, 5, Unit.PCT);
		cellTable2.addColumn(zoneColumn, "UTM Zone");

	    Column<XmlSimpleModel, String> eastingColumn = new BoundColumn(editable, EASTING);
		cellTable2.setColumnWidth(eastingColumn, 10, Unit.PCT);
		cellTable2.addColumn(eastingColumn, "UTM Easting");
		
	    Column<XmlSimpleModel, String> northingColumn = new BoundColumn(editable, NORTHING);
		cellTable2.setColumnWidth(northingColumn, 10, Unit.PCT);
		cellTable2.addColumn(northingColumn, "UTM Northing");
		
	    Column<XmlSimpleModel, String> heightColumn = new BoundColumn(editable, HEIGHT);
		cellTable2.setColumnWidth(heightColumn, 6, Unit.PCT);
		cellTable2.addColumn(heightColumn, "Height");

	    Column<XmlSimpleModel, String> latColumn = new BoundColumn(editable, LATITUDE);
		cellTable2.setColumnWidth(latColumn, 10, Unit.PCT);
		cellTable2.addColumn(latColumn, "Latitude");

	    Column<XmlSimpleModel, String> longColumn = new BoundColumn(editable, LONGITUDE);
		cellTable2.setColumnWidth(longColumn, 10, Unit.PCT);
		cellTable2.addColumn(longColumn, "Longitude");
		
	}
	
	static Cell<String> newStringCell(boolean editable)
	{
		return editable ? new EditTextCell() : new TextCell();
	}
	
//	static Cell<String> newNumberCell(boolean editable)
//	{
//		return editable ? new Edit : new TextCell();
//	}
	
	/*
	public class MapButtonClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent event)
		{
			NativeMapView.setPoints(nodes);
			
			// todo
			Controller.get().getEventBus().fireEvent(new ViewTransitionEvent(new ViewTransition("map", null, false, "Map")));
		}
		
	}*/
	

}
