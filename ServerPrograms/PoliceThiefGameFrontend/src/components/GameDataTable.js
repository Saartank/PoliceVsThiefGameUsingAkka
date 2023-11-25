import React from "react";

const DataTablePolice = ({ currentNode, data }) => {
  const title = "Police";
  return (
    <div className="w-1/2 p-2">
      <h3 className="text-lg font-semibold text-center mb-2">{title}</h3>
      <div className="text-center mb-4">
        <span className="font-medium">Current Node: </span>
        {currentNode}
      </div>
      <table className="table-auto border-collapse border border-slate-400 w-full">
        <thead>
          <tr>
            <th className="border border-slate-300 px-2 py-1">Adjacent Node</th>
            <th className="border border-slate-300 px-2 py-1">
              Distance to Thief
            </th>
            <th className="border border-slate-300 px-2 py-1">
              Distance to the valuable data node
            </th>
          </tr>
        </thead>
        <tbody>
          {Object.entries(data).map(([node, details]) => (
            <tr key={node}>
              <td className="border border-slate-300 px-2 py-1">{node}</td>
              <td className="border border-slate-300 px-2 py-1">
                {details[0]}
              </td>
              <td className="border border-slate-300 px-2 py-1">
                {details[1]}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

const DataTableThief = ({ currentNode, data }) => {
  const title = "Thief";
  return (
    <div className="w-1/2 p-2">
      <h3 className="text-lg font-semibold text-center mb-2">{title}</h3>
      <div className="text-center mb-4">
        <span className="font-medium">Current Node: </span>
        {currentNode}
      </div>
      <table className="table-auto border-collapse border border-slate-400 w-full">
        <thead>
          <tr>
            <th className="border border-slate-300 px-2 py-1">Adjacent Node</th>
            <th className="border border-slate-300 px-2 py-1">
              Confidence score
            </th>
            <th className="border border-slate-300 px-2 py-1">
              Distance from Police
            </th>
            <th className="border border-slate-300 px-2 py-1">
              Distance to the valuable data node
            </th>
          </tr>
        </thead>
        <tbody>
          {Object.entries(data).map(([node, details]) => (
            <tr key={node}>
              <td className="border border-slate-300 px-2 py-1">{node}</td>
              <td className="border border-slate-300 px-2 py-1">
                {details[0]}
              </td>
              <td className="border border-slate-300 px-2 py-1">
                {details[1]}
              </td>
              <td className="border border-slate-300 px-2 py-1">
                {details[2]}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

const GameDataTable = ({ response }) => {
  const message = response.message;
  const policeData = response.data[0];
  const thiefData = response.data[1];

  return (
    <div className="container mx-auto p-4">
      <div className="text-center font-bold text-xl mb-4">{message}</div>
      <div className="flex justify-between">
        <DataTablePolice currentNode={policeData[0][1]} data={policeData[1]} />
        <DataTableThief currentNode={thiefData[0][1]} data={thiefData[1]} />
      </div>
    </div>
  );
};

export default GameDataTable;
