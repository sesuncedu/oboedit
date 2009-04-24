package org.oboedit.gui.components.ontologyGeneration;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal representation of a definition
 * 
 * @author Atif Iqbal, 2008
 * @author Thomas Waechter (<href>waechter@biotec.tu-dresden.de</href>), 2008
 */
public class CandidateDefinition implements Cloneable
{
	private final int index;
	private final List<String> cachedURLs;
	private String definition;
	private List<CandidateDefinition> alternativeDefinitions;
	private String definitionHTMLFormatted;
	private boolean isTicked;
	private boolean isVisible;
	private List<UpdateListener> listeners = new ArrayList<UpdateListener>();
	private int parentTermCount;
	private final List<String> urls;

	/**
	 * Constructs a {@link CandidateDefinition}
	 * 
	 * @param index
	 * @param def
	 */
	public CandidateDefinition(int index, String def, String defFormatted)
	{
		this(index, def, defFormatted, null, null, 0, false);
	}

	/**
	 * Constructs a {@link CandidateDefinition}
	 * 
	 * @param index
	 * @param def
	 * @param ur
	 * @param cachedUR
	 */
	public CandidateDefinition(int index, String def, String defFormatted, String ur, String cachedUR)
	{
		this(index, def, defFormatted, ur, cachedUR, 0, false);
	}

	/**
	 * Constructs a {@link CandidateDefinition}
	 * 
	 * @param index
	 * @param def
	 * @param ur
	 * @param cachedUR
	 * @param termCount
	 * @param select
	 */
	public CandidateDefinition(int index, String def, String defFormatted, String ur, String cachedUR, int termCount, boolean select)
	{
		urls = new ArrayList<String>();
		cachedURLs = new ArrayList<String>();
		
		this.index = index;
		this.definition = def;
		this.definitionHTMLFormatted = defFormatted;
		this.urls.add(ur);
		this.cachedURLs.add(cachedUR);
		this.parentTermCount = termCount;
		this.isTicked = select;
		this.isVisible = true;
	}

	@Override
	public boolean equals(Object object)
	{
		return object instanceof CandidateDefinition && this.definition
		    .equals(((CandidateDefinition) object).definition);
	}

	@Override
	public int hashCode()
	{
		return this.definition.hashCode();
	}

	/**
	 * @return the URL of the location where the definition was extracted from
	 */
	public List<String> getUrl()
	{
		return urls;
	}

	/**
	 * @return the cached URL of the location where the definition was extracted from
	 */
	public List<String> getCachedURL()
	{
		return cachedURLs;
	}

	/**
	 * @return the definition
	 */
	public String getDefinition()
	{
		return definition;
	}

	/**
	 * @return the index
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * @return the number of known parents set
	 */
	public int getParentTermCount()
	{
		return parentTermCount;
	}

	/**
	 * @return <code>true</code> if the definition is ticked
	 */
	public boolean isTicked()
	{
		return isTicked;
	}

	/**
	 * @return <code>true</code> if the definition is currently set to visible (e.g. by filtering)
	 */
	public boolean isVisible()
	{
		return isVisible;
	}

	/**
	 * Set the definition string
	 * 
	 * @param definition the definition string
	 */
	public void setDefinition(String definition)
	{
		this.definition = definition;
	}

	/**
	 * Add an URL (source URL)
	 *
	 * @param url
	 */
	public void addURL(String url) 
	{
		this.urls.add(url);
	}
		
	/**
	 * Add and cacheURL 
	 *
	 * @param cachedURL
	 */
	public void addCachedURL(String cachedURL)
	{
		this.cachedURLs.add(cachedURL);
	}
	
	public void addAlternativeDefinition(CandidateDefinition candidateDefinition)
	{
		if (alternativeDefinitions == null) {
			alternativeDefinitions = new ArrayList<CandidateDefinition>();
		}

//		if (! alternativeDefinitions.contains(candidateDefinition)) {
			alternativeDefinitions.add(candidateDefinition);
//		}
	}
	
	public void resetAlternativeDefinitions()
	{
		alternativeDefinitions = null;
	}
	
	public List<CandidateDefinition> getAlternativeDefinitions() {
		return alternativeDefinitions;
	}
	
	/**
	 * Set parent term count
	 * 
	 * @param parentTermCount
	 */
	public void setParentTermCount(int parentTermCount)
	{
		this.parentTermCount = parentTermCount;
	}

	/**
	 * Set whether a definition is selected
	 * 
	 * @param isTicked
	 */
	public void setTicked(boolean isTicked)
	{
		if (this.isTicked == isTicked)
			return;
		this.isTicked = isTicked;
		notifyListeners();
	}

	/**
	 * Set whether a definition is visible
	 * 
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible)
	{
		this.isVisible = isVisible;
	}

	/**
	 * Add listeners to this {@link CandidateDefinition}
	 * 
	 * @param updateListner
	 */
	public void addListener(UpdateListener updateListner)
	{
		listeners.add(updateListner);
		if (listeners.size() > 1)
			throw new RuntimeException();
	}
	
	public void removeListeners() 
	{
		listeners.clear();
	}
	
	public List<UpdateListener> getListeners()
	{
		return listeners;
	}

	/**
	 * Notify all attached listeners
	 */
	private void notifyListeners()
	{
		for (UpdateListener l : listeners)
			l.update();
	}

	/**
     * @param definitionHTMLFormatted the definitionHTMLFormatted to set
     */
    public void setDefinitionHTMLFormatted(String definitionHTMLFormatted)
    {
	    this.definitionHTMLFormatted = definitionHTMLFormatted;
    }

	/**
     * @return the definitionHTMLFormatted
     */
    public String getDefinitionHTMLFormatted()
    {
	    return definitionHTMLFormatted;
    }
    
    @Override
    public CandidateDefinition clone()
    {
    	try {
			return (CandidateDefinition)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
}
