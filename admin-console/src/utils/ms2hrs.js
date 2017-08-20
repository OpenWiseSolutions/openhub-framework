export default function (ms) {
  if (!ms) {
    return ''
  }

  const time = new Date(ms)
  let str = ''

  str += time.getUTCDate() - 1 + 'd '
  str += time.getUTCHours() + 'h '
  str += time.getUTCMinutes() + 'm '

  return str
}

