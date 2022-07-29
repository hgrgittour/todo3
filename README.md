Because of time, it can only go so far.
Need to import the local mongodb json collection, after starting the project.
The logic is to determine the cedantid based on the input parameters region, country, group_cedant
branchid, slipsid, check out the data and then populate it. (This step can do correlation query)

The input can contain parameters of five dimensions.   
private Country country;
private Region region;
private GroupsCedant groupsCedant;
private Branche branche;
private SlipsPremium slipsPremium;
For each parameter a json can be constructed e.g.
{
"country":{"name": "COTE D'IVOIRE"},
"branche":{"name": "TRANSPORT CORPS ET FACULTES"}
}