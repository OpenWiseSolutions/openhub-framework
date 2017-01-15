export default function (...classes) {
  return classes
    .reduce((acc, item) => {
      if (item) acc.push(item)
      return acc
    }, [])
    .join(' ')
}
